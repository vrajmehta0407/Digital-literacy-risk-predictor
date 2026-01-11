package com.dlrp.app.detection

import com.dlrp.app.storage.EventLogStore
import com.dlrp.app.guardian.GuardianNotifier
import java.security.MessageDigest

/**
 * Tracks repeated scam attempts and auto-escalates protection level.
 * Notifies guardian when similar scam messages are detected repeatedly.
 */
class ScamPatternTracker(private val guardianNotifier: GuardianNotifier) {
    
    companion object {
        private const val PATTERN_WINDOW_MS = 24 * 60 * 60 * 1000L // 24 hours
        private const val REPEAT_THRESHOLD = 3 // Auto-escalate after 3 similar attempts
    }
    
    private val patternStore = mutableMapOf<String, PatternInfo>()
    
    /**
     * Track a scam message and check for repeated attempts.
     * @return True if this is a repeated scam attempt (>= threshold)
     */
    fun trackAndCheckRepeat(sender: String, messageBody: String, keywords: List<String>): RepeatDetectionResult {
        val fingerprint = generateFingerprint(sender, keywords)
        val now = System.currentTimeMillis()
        
        val patternInfo = patternStore.getOrPut(fingerprint) {
            PatternInfo(
                fingerprint = fingerprint,
                firstSeen = now,
                lastSeen = now,
                count = 0,
                sender = sender,
                keywords = keywords
            )
        }
        
        // Check if within time window
        if (now - patternInfo.firstSeen <= PATTERN_WINDOW_MS) {
            patternInfo.count++
            patternInfo.lastSeen = now
            
            if (patternInfo.count >= REPEAT_THRESHOLD) {
                // Repeated scam detected!
                logRepeatEvent(patternInfo)
                notifyGuardian(patternInfo)
                
                return RepeatDetectionResult(
                    isRepeat = true,
                    count = patternInfo.count,
                    shouldEscalate = true,
                    message = "⚠️ REPEATED SCAM ATTEMPT! This is the ${patternInfo.count}th similar scam message. " +
                             "Protection level increased. Guardian notified."
                )
            }
        } else {
            // Outside window, reset
            patternInfo.firstSeen = now
            patternInfo.lastSeen = now
            patternInfo.count = 1
        }
        
        return RepeatDetectionResult(
            isRepeat = false,
            count = patternInfo.count,
            shouldEscalate = false,
            message = ""
        )
    }
    
    /**
     * Generate fingerprint from sender and keywords.
     */
    private fun generateFingerprint(sender: String, keywords: List<String>): String {
        val combined = "$sender:${keywords.sorted().joinToString(",")}"
        return hashString(combined)
    }
    
    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    private fun logRepeatEvent(patternInfo: PatternInfo) {
        EventLogStore.logEvent(
            type = "REPEATED_SCAM_ATTEMPT",
            details = "Repeated scam from ${patternInfo.sender}, count: ${patternInfo.count}",
            riskLevel = "DANGER"
        )
    }
    
    private fun notifyGuardian(patternInfo: PatternInfo) {
        guardianNotifier.sendAlert(
            "Repeated Scam Alert",
            "Your loved one has received ${patternInfo.count} similar scam messages in the last 24 hours. " +
            "Protection level has been increased automatically."
        )
    }
    
    /**
     * Clean up old patterns outside the time window.
     */
    fun cleanupOldPatterns() {
        val now = System.currentTimeMillis()
        patternStore.entries.removeIf { (_, info) ->
            now - info.lastSeen > PATTERN_WINDOW_MS
        }
    }
    
    data class PatternInfo(
        val fingerprint: String,
        var firstSeen: Long,
        var lastSeen: Long,
        var count: Int,
        val sender: String,
        val keywords: List<String>
    )
    
    data class RepeatDetectionResult(
        val isRepeat: Boolean,
        val count: Int,
        val shouldEscalate: Boolean,
        val message: String
    )
}

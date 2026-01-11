package com.dlrp.app.detection

import android.content.Context
import com.dlrp.app.storage.LearnedPatternsStore

/**
 * Adaptive learning engine that learns from user-confirmed scams.
 * Improves detection accuracy over time without requiring internet.
 */
class AdaptiveLearningEngine(private val context: Context) {
    
    private val learnedPatternsStore = LearnedPatternsStore(context)
    
    /**
     * Learn from a confirmed scam message.
     * Extracts new keywords and patterns to improve future detection.
     */
    fun learnFromScam(sender: String, messageBody: String, userConfirmed: Boolean) {
        if (!userConfirmed) return
        
        // Extract new keywords
        val newKeywords = extractKeywords(messageBody)
        
        // Extract patterns
        val patterns = extractPatterns(messageBody)
        
        // Store learned keywords
        newKeywords.forEach { keyword ->
            learnedPatternsStore.addLearnedKeyword(keyword, sender)
        }
        
        // Store learned patterns
        patterns.forEach { pattern ->
            learnedPatternsStore.addLearnedPattern(pattern, sender)
        }
        
        // Log learning event
        learnedPatternsStore.logLearningEvent(
            "Learned ${newKeywords.size} keywords and ${patterns.size} patterns from scam"
        )
    }
    
    /**
     * Extract potential scam keywords from message.
     */
    private fun extractKeywords(message: String): List<String> {
        val keywords = mutableListOf<String>()
        val words = message.lowercase().split(Regex("\\s+"))
        
        // Look for suspicious words
        val suspiciousIndicators = listOf(
            "urgent", "immediately", "expire", "suspend", "block", "verify",
            "update", "confirm", "click", "link", "prize", "winner", "congratulations",
            "account", "bank", "card", "otp", "password", "pin"
        )
        
        words.forEach { word ->
            if (word.length >= 4 && suspiciousIndicators.any { word.contains(it) }) {
                keywords.add(word)
            }
        }
        
        return keywords.distinct()
    }
    
    /**
     * Extract patterns (phrases) from message.
     */
    private fun extractPatterns(message: String): List<String> {
        val patterns = mutableListOf<String>()
        
        // Extract 2-3 word phrases
        val words = message.split(Regex("\\s+"))
        for (i in 0 until words.size - 1) {
            val twoWordPhrase = "${words[i]} ${words[i + 1]}".lowercase()
            if (twoWordPhrase.length >= 8) {
                patterns.add(twoWordPhrase)
            }
            
            if (i < words.size - 2) {
                val threeWordPhrase = "${words[i]} ${words[i + 1]} ${words[i + 2]}".lowercase()
                if (threeWordPhrase.length >= 12) {
                    patterns.add(threeWordPhrase)
                }
            }
        }
        
        return patterns.distinct()
    }
    
    /**
     * Get all learned keywords for detection.
     */
    fun getLearnedKeywords(): List<String> {
        return learnedPatternsStore.getAllLearnedKeywords()
    }
    
    /**
     * Get all learned patterns for detection.
     */
    fun getLearnedPatterns(): List<String> {
        return learnedPatternsStore.getAllLearnedPatterns()
    }
    
    /**
     * Check if message matches learned patterns.
     */
    fun matchesLearnedPatterns(message: String): MatchResult {
        val lowerMessage = message.lowercase()
        val matchedKeywords = mutableListOf<String>()
        val matchedPatterns = mutableListOf<String>()
        
        // Check learned keywords
        getLearnedKeywords().forEach { keyword ->
            if (lowerMessage.contains(keyword.lowercase())) {
                matchedKeywords.add(keyword)
            }
        }
        
        // Check learned patterns
        getLearnedPatterns().forEach { pattern ->
            if (lowerMessage.contains(pattern.lowercase())) {
                matchedPatterns.add(pattern)
            }
        }
        
        val hasMatch = matchedKeywords.isNotEmpty() || matchedPatterns.isNotEmpty()
        
        return MatchResult(
            hasMatch = hasMatch,
            matchedKeywords = matchedKeywords,
            matchedPatterns = matchedPatterns,
            confidence = calculateConfidence(matchedKeywords.size, matchedPatterns.size)
        )
    }
    
    private fun calculateConfidence(keywordCount: Int, patternCount: Int): Float {
        // Simple confidence calculation
        val score = (keywordCount * 0.3f) + (patternCount * 0.7f)
        return (score / 5f).coerceIn(0f, 1f)
    }
    
    data class MatchResult(
        val hasMatch: Boolean,
        val matchedKeywords: List<String>,
        val matchedPatterns: List<String>,
        val confidence: Float
    )
}

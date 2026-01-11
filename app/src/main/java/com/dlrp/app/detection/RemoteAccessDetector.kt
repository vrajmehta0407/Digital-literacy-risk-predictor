package com.dlrp.app.detection

/**
 * Detects remote access and screen sharing keywords to warn users about scams.
 * Triggers CRITICAL alerts when remote access apps are mentioned.
 */
class RemoteAccessDetector {
    
    companion object {
        // Remote access app keywords (English)
        private val REMOTE_ACCESS_KEYWORDS_EN = setOf(
            "anydesk", "teamviewer", "quicksupport", "remotely", "screen share",
            "screen sharing", "remote access", "remote control", "remote desktop",
            "share screen", "chrome remote", "ammyy", "supremo", "ultraviewer",
            "rustdesk", "zoho assist", "logmein", "gotomypc"
        )
        
        // Remote access keywords (Hindi - transliterated)
        private val REMOTE_ACCESS_KEYWORDS_HI = setOf(
            "screen share karo", "screen dikhao", "remote access", "apna screen share",
            "screen dikhaiye", "anydesk download", "teamviewer install"
        )
        
        // Remote access keywords (Gujarati - transliterated)
        private val REMOTE_ACCESS_KEYWORDS_GU = setOf(
            "screen share karo", "screen batavo", "remote access", "screen dekhavo"
        )
        
        // Instruction phrases that indicate scam
        private val SCAM_INSTRUCTION_PHRASES = setOf(
            "download and install", "install this app", "give me access",
            "allow remote", "share your screen", "show me your screen",
            "download karo", "install karo", "access do"
        )
    }
    
    /**
     * Check if message contains remote access keywords.
     * @return Detection result with risk level and matched keywords
     */
    fun detectRemoteAccess(messageBody: String): RemoteAccessDetection {
        val lowerMessage = messageBody.lowercase()
        val matchedKeywords = mutableListOf<String>()
        
        // Check English keywords
        REMOTE_ACCESS_KEYWORDS_EN.forEach { keyword ->
            if (lowerMessage.contains(keyword)) {
                matchedKeywords.add(keyword)
            }
        }
        
        // Check Hindi keywords
        REMOTE_ACCESS_KEYWORDS_HI.forEach { keyword ->
            if (lowerMessage.contains(keyword)) {
                matchedKeywords.add(keyword)
            }
        }
        
        // Check Gujarati keywords
        REMOTE_ACCESS_KEYWORDS_GU.forEach { keyword ->
            if (lowerMessage.contains(keyword)) {
                matchedKeywords.add(keyword)
            }
        }
        
        // Check for scam instruction phrases
        val hasScamInstructions = SCAM_INSTRUCTION_PHRASES.any { 
            lowerMessage.contains(it) 
        }
        
        if (matchedKeywords.isNotEmpty()) {
            return RemoteAccessDetection(
                detected = true,
                isCritical = true,
                matchedKeywords = matchedKeywords,
                warningMessage = getWarningMessage(hasScamInstructions),
                voiceWarning = getVoiceWarning()
            )
        }
        
        return RemoteAccessDetection(
            detected = false,
            isCritical = false,
            matchedKeywords = emptyList(),
            warningMessage = "",
            voiceWarning = ""
        )
    }
    
    private fun getWarningMessage(hasInstructions: Boolean): String {
        return if (hasInstructions) {
            "🚨 CRITICAL DANGER! Someone is asking you to install remote access software. " +
            "This is a SCAM! Never install AnyDesk, TeamViewer, or share your screen with anyone. " +
            "Hang up immediately!"
        } else {
            "🚨 WARNING! This message mentions remote access software. " +
            "Never install such apps or share your screen unless you are 100% sure it's safe."
        }
    }
    
    private fun getVoiceWarning(): String {
        return "Critical danger detected! Someone is trying to access your phone remotely. " +
               "This is a scam. Do not install any app. Hang up immediately."
    }
    
    data class RemoteAccessDetection(
        val detected: Boolean,
        val isCritical: Boolean,
        val matchedKeywords: List<String>,
        val warningMessage: String,
        val voiceWarning: String
    )
}

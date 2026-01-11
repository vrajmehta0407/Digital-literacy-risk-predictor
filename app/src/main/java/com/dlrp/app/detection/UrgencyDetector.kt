package com.dlrp.app.detection

class UrgencyDetector {

    private val URGENCY_PHRASES = listOf(
        "immediately", "now", "today only", "within 24 hours",
        "action required", "act now", "limited time", "expires soon",
        "don't wait", "risk of suspension", "legal action"
    )

    fun hasUrgency(text: String): Boolean {
        val lowerText = text.lowercase()
        return URGENCY_PHRASES.any { lowerText.contains(it) }
    }
}

package com.dlrp.app.detection

import java.util.regex.Pattern

class KeywordDetector {

    private val SUSPICIOUS_KEYWORDS = listOf(
        "winner", "won", "prize", "lottery", "urgent", "account blocked",
        "verify", "kyc", "suspend", "expiration", "unusual activity",
        "refund", "claim", "deposit", "investment", "profit",
        "otp", "pin", "password", "cvv", "atm card", "credit card"
    )

    fun findMatches(text: String): List<String> {
        val lowerText = text.lowercase()
        return SUSPICIOUS_KEYWORDS.filter { lowerText.contains(it) }
    }
}

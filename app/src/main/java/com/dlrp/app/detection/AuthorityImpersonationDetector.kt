package com.dlrp.app.detection

class AuthorityImpersonationDetector {

    private val AUTHORITY_KEYWORDS = listOf(
        "police", "cbi", "rbi", "income tax", "customs",
        "irs", "court", "warrant", "arrest", "manager"
    )

    fun detectImpersonation(text: String): Boolean {
        val lower = text.lowercase()
        return AUTHORITY_KEYWORDS.any { lower.contains(it) }
    }
}

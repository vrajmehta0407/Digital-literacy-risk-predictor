package com.dlrp.app.detection

object OfflineRuleStore {
    // This serves as a local database for regex rules
    // that can be updated over the air if needed.
    
    val BLOCKED_DOMAINS = listOf(
        "bit.ly", "tinyurl.com", "goo.gl" // Shorteners often used in scams
    )
    
    val SAFE_SENDERS = listOf(
        "HDFC", "SBI", "ICICI", "GOV", "ADHAAR"
    )
}

package com.dlrp.app.sms

import com.dlrp.app.risk.RiskLevel

class SmsRiskClassifier {
    // Additional granular classification if needed
    // e.g. "Banking Fraud", "Lottery Scam", "Grandchild Scam"
    
    fun classifyType(body: String): String {
        val lower = body.lowercase()
        return when {
            lower.contains("winner") -> "Lottery Scam"
            lower.contains("verify") && lower.contains("bank") -> "Bank Phishing"
            else -> "General Suspicion"
        }
    }
}

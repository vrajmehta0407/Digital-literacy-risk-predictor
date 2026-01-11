package com.dlrp.app.risk

class RiskDecisionEngine {

    fun determineRisk(
        hasKeywords: Boolean,
        hasUrgency: Boolean,
        hasLink: Boolean,
        hasCode: Boolean,
        isUnknownSender: Boolean
    ): RiskLevel {
        
        // DANGER: Code + Link (Classic Phishing)
        if (hasCode && hasLink) return RiskLevel.DANGER

        // DANGER: Urgency + Link or Code
        if (hasUrgency && (hasLink || hasCode)) return RiskLevel.DANGER

        // DANGER: Unknown Sender + (Code OR Link OR Urgency)
        if (isUnknownSender && (hasCode || hasLink || hasUrgency)) return RiskLevel.DANGER

        // CAUTION: Keywords present but no immediate critical threat detected
        if (hasKeywords) return RiskLevel.CAUTION

        // CAUTION: Just a Link from unknown sender (could be spam)
        if (isUnknownSender && hasLink) return RiskLevel.CAUTION

        // SAFE: If none of the above match
        return RiskLevel.SAFE
    }
}

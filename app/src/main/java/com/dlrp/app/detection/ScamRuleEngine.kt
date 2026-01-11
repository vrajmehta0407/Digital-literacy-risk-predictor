package com.dlrp.app.detection

import android.content.Context
import com.dlrp.app.risk.RiskDecisionEngine
import com.dlrp.app.risk.RiskLevel
import com.dlrp.app.risk.RiskState
import com.dlrp.app.otp.TrustedSenderRegistry
import com.dlrp.app.guardian.GuardianNotifier

class ScamRuleEngine(private val context: Context) {

    private val keywordDetector = KeywordDetector()
    private val urgencyDetector = UrgencyDetector()
    private val linkAndCodeDetector = LinkAndCodeDetector()
    private val decisionEngine = RiskDecisionEngine()
    private val trustedSenderRegistry = TrustedSenderRegistry()
    
    // New enhanced detectors
    private val callOTPCorrelator = CallOTPCorrelator()
    private val linkSafetyAnalyzer = LinkSafetyAnalyzer()
    private val remoteAccessDetector = RemoteAccessDetector()
    private val multilingualDetector = MultilingualDetector()
    private val bankNameValidator = BankNameValidator()
    private val adaptiveLearningEngine = AdaptiveLearningEngine(context)
    private val scamPatternTracker = ScamPatternTracker(GuardianNotifier(context))

    fun analyze(sender: String, messageBody: String): RiskState {
        // 1. Check Trusted List
        if (trustedSenderRegistry.isTrusted(sender)) {
            return RiskState(RiskLevel.SAFE, listOf("Trusted Sender"))
        }

        val reasons = mutableListOf<String>()
        var riskLevel = RiskLevel.CAUTION
        val keywords = mutableListOf<String>()
        
        // 2. Check for Call-OTP Correlation (CRITICAL)
        if (callOTPCorrelator.hasCallOTPCorrelation(sender, messageBody)) {
            riskLevel = RiskLevel.DANGER
            reasons.add("⚠️ SCAM ALERT: Suspicious call + OTP detected!")
            callOTPCorrelator.logCorrelationEvent(sender, messageBody)
        }
        
        // 3. Check for Remote Access Attempts (CRITICAL)
        val remoteAccessCheck = remoteAccessDetector.detectRemoteAccess(messageBody)
        if (remoteAccessCheck.detected) {
            riskLevel = RiskLevel.DANGER
            reasons.add("🚨 CRITICAL: Remote access attempt detected!")
            keywords.addAll(remoteAccessCheck.matchedKeywords)
        }
        
        // 4. Validate Bank Name
        val bankValidation = bankNameValidator.validateBankName(sender, messageBody)
        if (bankValidation.isFake) {
            riskLevel = RiskLevel.DANGER
            reasons.add("🚨 FAKE BANK: ${bankValidation.warningMessage}")
        }
        
        // 5. Check Link Safety
        val hasLink = linkAndCodeDetector.containsLink(messageBody)
        if (hasLink) {
            val links = extractLinks(messageBody)
            links.forEach { link ->
                val linkSafety = linkSafetyAnalyzer.analyzeLink(link)
                if (linkSafety.isRisky) {
                    if (linkSafety.riskLevel == LinkSafetyAnalyzer.RiskLevel.CRITICAL) {
                        riskLevel = RiskLevel.DANGER
                    }
                    reasons.add(linkSafety.message)
                }
            }
        }
        
        // 6. Run Standard Detectors
        val detectedKeywords = keywordDetector.findMatches(messageBody)
        keywords.addAll(detectedKeywords)
        val hasUrgency = urgencyDetector.hasUrgency(messageBody)
        val hasCode = linkAndCodeDetector.containsCode(messageBody)
        
        // 7. Check Multilingual Content
        val multilingualCheck = multilingualDetector.detectMultilingual(messageBody)
        if (multilingualCheck.hasMultilingualContent) {
            keywords.addAll(multilingualCheck.matchedKeywords.map { it.keyword })
            if (multilingualCheck.riskScore > 0.5f) {
                reasons.add("Suspicious multilingual content detected")
            }
        }
        
        // 8. Check Learned Patterns
        val learnedMatch = adaptiveLearningEngine.matchesLearnedPatterns(messageBody)
        if (learnedMatch.hasMatch) {
            keywords.addAll(learnedMatch.matchedKeywords)
            reasons.add("Matches previously learned scam pattern")
        }
        
        // 9. Check for Repeated Scam Attempts
        val repeatCheck = scamPatternTracker.trackAndCheckRepeat(sender, messageBody, keywords)
        if (repeatCheck.isRepeat) {
            riskLevel = RiskLevel.DANGER
            reasons.add(repeatCheck.message)
        }
        
        // 10. Determine Final Risk Level (if not already set to DANGER)
        if (riskLevel != RiskLevel.DANGER) {
            riskLevel = decisionEngine.determineRisk(
                hasKeywords = keywords.isNotEmpty(),
                hasUrgency = hasUrgency,
                hasLink = hasLink,
                hasCode = hasCode,
                isUnknownSender = true
            )
        }

        // 11. Add Standard Reasons
        if (hasUrgency) reasons.add("Urgent language detected")
        if (hasLink && reasons.none { it.contains("link") }) reasons.add("Contains suspicious link")
        if (hasCode) reasons.add("Requesting or sending code")
        if (keywords.isNotEmpty() && reasons.isEmpty()) reasons.add("Suspicious keywords found")

        val extractedCode = if (hasCode) linkAndCodeDetector.extractCode(messageBody) else null

        return RiskState(riskLevel, reasons, keywords, extractedCode)
    }
    
    /**
     * Learn from user-confirmed scam.
     */
    fun learnFromScam(sender: String, messageBody: String, userConfirmed: Boolean) {
        adaptiveLearningEngine.learnFromScam(sender, messageBody, userConfirmed)
    }
    
    private fun extractLinks(message: String): List<String> {
        val urlPattern = Regex("(https?://[^\\s]+)")
        return urlPattern.findAll(message).map { it.value }.toList()
    }
}

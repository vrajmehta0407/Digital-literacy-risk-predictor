package com.dlrp.app.detection

import java.net.URL
import java.net.HttpURLConnection

/**
 * Analyzes links for safety without requiring user to click.
 * Detects risky links and provides safety previews.
 */
class LinkSafetyAnalyzer {
    
    companion object {
        // Known phishing domains
        private val PHISHING_DOMAINS = setOf(
            "bit.ly", "tinyurl.com", "goo.gl", "ow.ly", "t.co",
            "short.link", "cutt.ly", "rb.gy", "is.gd"
        )
        
        // Suspicious TLDs often used in scams
        private val SUSPICIOUS_TLDS = setOf(
            ".tk", ".ml", ".ga", ".cf", ".gq", ".xyz", ".top", ".work"
        )
        
        // Legitimate bank domains for comparison
        private val LEGITIMATE_BANK_DOMAINS = setOf(
            "sbi.co.in", "onlinesbi.sbi", "icicibank.com", "hdfcbank.com",
            "axisbank.com", "kotak.com", "pnbindia.in", "bankofbaroda.in"
        )
    }
    
    /**
     * Analyze a URL for safety.
     * @return SafetyResult with risk level and message
     */
    fun analyzeLink(url: String): SafetyResult {
        try {
            val normalizedUrl = normalizeUrl(url)
            val domain = extractDomain(normalizedUrl)
            
            // Check if it's a URL shortener
            if (isUrlShortener(domain)) {
                return SafetyResult(
                    isRisky = true,
                    riskLevel = RiskLevel.HIGH,
                    message = "🔴 This link may steal your information. Do not open.",
                    reason = "URL shortener detected - cannot verify destination"
                )
            }
            
            // Check for suspicious TLD
            if (hasSuspiciousTLD(domain)) {
                return SafetyResult(
                    isRisky = true,
                    riskLevel = RiskLevel.HIGH,
                    message = "🔴 This link may steal your information. Do not open.",
                    reason = "Suspicious domain extension detected"
                )
            }
            
            // Check for fake bank domain
            val fakeBankCheck = checkFakeBankDomain(domain)
            if (fakeBankCheck.isFake) {
                return SafetyResult(
                    isRisky = true,
                    riskLevel = RiskLevel.CRITICAL,
                    message = "🔴 FAKE BANK LINK! This is NOT the real ${fakeBankCheck.realBank}. Do not open!",
                    reason = "Fake bank domain detected"
                )
            }
            
            // Check if domain contains suspicious keywords
            if (containsSuspiciousKeywords(domain)) {
                return SafetyResult(
                    isRisky = true,
                    riskLevel = RiskLevel.MEDIUM,
                    message = "⚠️ This link looks suspicious. Be careful before opening.",
                    reason = "Suspicious keywords in domain"
                )
            }
            
            // Link appears safe
            return SafetyResult(
                isRisky = false,
                riskLevel = RiskLevel.LOW,
                message = "Link appears safe, but always verify before clicking.",
                reason = "No obvious threats detected"
            )
            
        } catch (e: Exception) {
            return SafetyResult(
                isRisky = true,
                riskLevel = RiskLevel.MEDIUM,
                message = "⚠️ Cannot verify this link. Be cautious.",
                reason = "Link analysis failed: ${e.message}"
            )
        }
    }
    
    private fun normalizeUrl(url: String): String {
        var normalized = url.trim().lowercase()
        if (!normalized.startsWith("http")) {
            normalized = "https://$normalized"
        }
        return normalized
    }
    
    private fun extractDomain(url: String): String {
        return try {
            val urlObj = URL(url)
            urlObj.host.lowercase()
        } catch (e: Exception) {
            url
        }
    }
    
    private fun isUrlShortener(domain: String): Boolean {
        return PHISHING_DOMAINS.any { domain.contains(it) }
    }
    
    private fun hasSuspiciousTLD(domain: String): Boolean {
        return SUSPICIOUS_TLDS.any { domain.endsWith(it) }
    }
    
    private fun checkFakeBankDomain(domain: String): FakeBankResult {
        for (legitDomain in LEGITIMATE_BANK_DOMAINS) {
            val bankName = legitDomain.split(".")[0].uppercase()
            
            // Check for variations like "sbi-help.com", "hdfc-care.com"
            if (domain.contains(bankName.lowercase()) && !domain.contains(legitDomain)) {
                return FakeBankResult(true, bankName)
            }
        }
        return FakeBankResult(false, "")
    }
    
    private fun containsSuspiciousKeywords(domain: String): Boolean {
        val suspiciousKeywords = listOf(
            "verify", "update", "secure", "account", "login", "confirm",
            "suspended", "blocked", "urgent", "action", "required"
        )
        return suspiciousKeywords.any { domain.contains(it) }
    }
    
    data class SafetyResult(
        val isRisky: Boolean,
        val riskLevel: RiskLevel,
        val message: String,
        val reason: String
    )
    
    data class FakeBankResult(
        val isFake: Boolean,
        val realBank: String
    )
    
    enum class RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}

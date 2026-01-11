package com.dlrp.app.detection

/**
 * Validates bank names and detects fake/similar bank names.
 * Detects variations like "SBI-Help", "HDFCBank-Care" that impersonate legitimate banks.
 */
class BankNameValidator {
    
    companion object {
        // Legitimate bank names and their official domains
        private val LEGITIMATE_BANKS = mapOf(
            "SBI" to listOf("sbi.co.in", "onlinesbi.sbi"),
            "HDFC" to listOf("hdfcbank.com"),
            "ICICI" to listOf("icicibank.com"),
            "AXIS" to listOf("axisbank.com"),
            "KOTAK" to listOf("kotak.com"),
            "PNB" to listOf("pnbindia.in"),
            "BOB" to listOf("bankofbaroda.in", "bankofbaroda.com"),
            "CANARA" to listOf("canarabank.com"),
            "UNION" to listOf("unionbankofindia.co.in"),
            "IDBI" to listOf("idbibank.in")
        )
        
        // Common fake suffixes
        private val FAKE_SUFFIXES = listOf(
            "-help", "-care", "-support", "-service", "-alert",
            "-info", "-update", "-verify", "-secure", "-official"
        )
    }
    
    /**
     * Validate sender name and detect fake bank impersonation.
     */
    fun validateBankName(sender: String, messageBody: String = ""): ValidationResult {
        val upperSender = sender.uppercase()
        
        // Check each legitimate bank
        for ((bankName, domains) in LEGITIMATE_BANKS) {
            if (upperSender.contains(bankName)) {
                // Check if it's a fake variation
                val fakeCheck = checkForFakeVariation(upperSender, bankName)
                if (fakeCheck.isFake) {
                    return ValidationResult(
                        isLegitimate = false,
                        isFake = true,
                        bankName = bankName,
                        fakePattern = fakeCheck.pattern,
                        warningMessage = "🚨 FAKE BANK ALERT! This is NOT the real $bankName. " +
                                       "Real $bankName never uses \"${fakeCheck.pattern}\". This is a SCAM!",
                        similarity = calculateSimilarity(upperSender, bankName)
                    )
                }
                
                // Check if it's legitimate (exact match or known pattern)
                if (isLegitimatePattern(upperSender, bankName)) {
                    return ValidationResult(
                        isLegitimate = true,
                        isFake = false,
                        bankName = bankName,
                        fakePattern = "",
                        warningMessage = "",
                        similarity = 1.0f
                    )
                }
            }
        }
        
        // Check for near-matches (typosquatting)
        val nearMatch = findNearMatch(upperSender)
        if (nearMatch != null) {
            return ValidationResult(
                isLegitimate = false,
                isFake = true,
                bankName = nearMatch.bankName,
                fakePattern = "similar spelling",
                warningMessage = "⚠️ WARNING! This looks similar to ${nearMatch.bankName} but is NOT the real bank. " +
                               "Be very careful!",
                similarity = nearMatch.similarity
            )
        }
        
        return ValidationResult(
            isLegitimate = false,
            isFake = false,
            bankName = "",
            fakePattern = "",
            warningMessage = "",
            similarity = 0f
        )
    }
    
    /**
     * Check for fake variations with suspicious suffixes.
     */
    private fun checkForFakeVariation(sender: String, bankName: String): FakeCheck {
        for (suffix in FAKE_SUFFIXES) {
            if (sender.contains("$bankName$suffix", ignoreCase = true)) {
                return FakeCheck(true, suffix)
            }
        }
        return FakeCheck(false, "")
    }
    
    /**
     * Check if sender matches legitimate bank pattern.
     */
    private fun isLegitimatePattern(sender: String, bankName: String): Boolean {
        // Exact match
        if (sender == bankName) return true
        
        // Known legitimate patterns (e.g., "SBI-ALERT", "HDFCBK")
        val legitimatePatterns = mapOf(
            "SBI" to listOf("SBI", "SBIALERT", "SBIINB"),
            "HDFC" to listOf("HDFC", "HDFCBK", "HDFCBANK"),
            "ICICI" to listOf("ICICI", "ICICIB", "ICICIBANK"),
            "AXIS" to listOf("AXIS", "AXISBK", "AXISBANK")
        )
        
        return legitimatePatterns[bankName]?.any { sender.contains(it) } ?: false
    }
    
    /**
     * Find near-match using Levenshtein distance.
     */
    private fun findNearMatch(sender: String): NearMatch? {
        for ((bankName, _) in LEGITIMATE_BANKS) {
            val similarity = calculateSimilarity(sender, bankName)
            if (similarity > 0.7f && similarity < 1.0f) {
                return NearMatch(bankName, similarity)
            }
        }
        return null
    }
    
    /**
     * Calculate similarity using Levenshtein distance.
     */
    private fun calculateSimilarity(s1: String, s2: String): Float {
        val longer = if (s1.length > s2.length) s1 else s2
        val shorter = if (s1.length > s2.length) s2 else s1
        
        if (longer.isEmpty()) return 1.0f
        
        val distance = levenshteinDistance(longer, shorter)
        return (longer.length - distance).toFloat() / longer.length
    }
    
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val costs = IntArray(s2.length + 1)
        for (i in 0..s1.length) {
            var lastValue = i
            for (j in 0..s2.length) {
                if (i == 0) {
                    costs[j] = j
                } else if (j > 0) {
                    var newValue = costs[j - 1]
                    if (s1[i - 1] != s2[j - 1]) {
                        newValue = minOf(minOf(newValue, lastValue), costs[j]) + 1
                    }
                    costs[j - 1] = lastValue
                    lastValue = newValue
                }
            }
            if (i > 0) costs[s2.length] = lastValue
        }
        return costs[s2.length]
    }
    
    data class ValidationResult(
        val isLegitimate: Boolean,
        val isFake: Boolean,
        val bankName: String,
        val fakePattern: String,
        val warningMessage: String,
        val similarity: Float
    )
    
    data class FakeCheck(
        val isFake: Boolean,
        val pattern: String
    )
    
    data class NearMatch(
        val bankName: String,
        val similarity: Float
    )
}

package com.dlrp.app.detection

/**
 * Detects scam messages in multiple languages including code-mixed content.
 * Supports English, Hindi, Gujarati, Tamil, Telugu, and Bengali.
 */
class MultilingualDetector {
    
    companion object {
        // Scam keywords in Hindi (Devanagari and transliterated)
        private val HINDI_KEYWORDS = mapOf(
            "खाता" to "account",
            "ब्लॉक" to "block",
            "तुरंत" to "urgent",
            "ओटीपी" to "otp",
            "पासवर्ड" to "password",
            "बैंक" to "bank",
            "aapka" to "your",
            "khata" to "account",
            "block" to "block",
            "turant" to "urgent",
            "jaldi" to "quickly",
            "zaruri" to "important"
        )
        
        // Scam keywords in Gujarati (transliterated)
        private val GUJARATI_KEYWORDS = mapOf(
            "tamaru" to "your",
            "khatu" to "account",
            "block" to "block",
            "jaldi" to "quickly",
            "jaruri" to "important",
            "bank" to "bank",
            "otp" to "otp"
        )
        
        // Scam keywords in Tamil (transliterated)
        private val TAMIL_KEYWORDS = mapOf(
            "ungal" to "your",
            "kanakku" to "account",
            "block" to "block",
            "udane" to "immediately",
            "bank" to "bank",
            "otp" to "otp"
        )
        
        // Scam keywords in Telugu (transliterated)
        private val TELUGU_KEYWORDS = mapOf(
            "mee" to "your",
            "khaata" to "account",
            "block" to "block",
            "twaraga" to "quickly",
            "bank" to "bank",
            "otp" to "otp"
        )
        
        // Scam keywords in Bengali (transliterated)
        private val BENGALI_KEYWORDS = mapOf(
            "apnar" to "your",
            "khata" to "account",
            "block" to "block",
            "tara" to "urgent",
            "bank" to "bank",
            "otp" to "otp"
        )
        
        // Common code-mixed phrases
        private val CODE_MIXED_PHRASES = listOf(
            "aapka account block",
            "your khata block",
            "otp share karo",
            "otp bhejo",
            "password do",
            "link click karo",
            "tamaru account block",
            "ungal kanakku block"
        )
    }
    
    /**
     * Detect scam content in multilingual messages.
     */
    fun detectMultilingual(message: String): MultilingualDetection {
        val lowerMessage = message.lowercase()
        val detectedLanguages = mutableSetOf<String>()
        val matchedKeywords = mutableListOf<MatchedKeyword>()
        
        // Check Hindi
        HINDI_KEYWORDS.forEach { (keyword, meaning) ->
            if (lowerMessage.contains(keyword.lowercase())) {
                detectedLanguages.add("Hindi")
                matchedKeywords.add(MatchedKeyword(keyword, meaning, "Hindi"))
            }
        }
        
        // Check Gujarati
        GUJARATI_KEYWORDS.forEach { (keyword, meaning) ->
            if (lowerMessage.contains(keyword.lowercase())) {
                detectedLanguages.add("Gujarati")
                matchedKeywords.add(MatchedKeyword(keyword, meaning, "Gujarati"))
            }
        }
        
        // Check Tamil
        TAMIL_KEYWORDS.forEach { (keyword, meaning) ->
            if (lowerMessage.contains(keyword.lowercase())) {
                detectedLanguages.add("Tamil")
                matchedKeywords.add(MatchedKeyword(keyword, meaning, "Tamil"))
            }
        }
        
        // Check Telugu
        TELUGU_KEYWORDS.forEach { (keyword, meaning) ->
            if (lowerMessage.contains(keyword.lowercase())) {
                detectedLanguages.add("Telugu")
                matchedKeywords.add(MatchedKeyword(keyword, meaning, "Telugu"))
            }
        }
        
        // Check Bengali
        BENGALI_KEYWORDS.forEach { (keyword, meaning) ->
            if (lowerMessage.contains(keyword.lowercase())) {
                detectedLanguages.add("Bengali")
                matchedKeywords.add(MatchedKeyword(keyword, meaning, "Bengali"))
            }
        }
        
        // Check code-mixed phrases
        val codeMixedMatches = CODE_MIXED_PHRASES.filter { phrase ->
            lowerMessage.contains(phrase.lowercase())
        }
        
        val isCodeMixed = detectedLanguages.size > 1 || codeMixedMatches.isNotEmpty()
        
        return MultilingualDetection(
            hasMultilingualContent = matchedKeywords.isNotEmpty(),
            detectedLanguages = detectedLanguages.toList(),
            isCodeMixed = isCodeMixed,
            matchedKeywords = matchedKeywords,
            codeMixedPhrases = codeMixedMatches,
            riskScore = calculateRiskScore(matchedKeywords.size, codeMixedMatches.size)
        )
    }
    
    private fun calculateRiskScore(keywordCount: Int, phrasesCount: Int): Float {
        val score = (keywordCount * 0.5f) + (phrasesCount * 1.0f)
        return (score / 5f).coerceIn(0f, 1f)
    }
    
    data class MatchedKeyword(
        val keyword: String,
        val meaning: String,
        val language: String
    )
    
    data class MultilingualDetection(
        val hasMultilingualContent: Boolean,
        val detectedLanguages: List<String>,
        val isCodeMixed: Boolean,
        val matchedKeywords: List<MatchedKeyword>,
        val codeMixedPhrases: List<String>,
        val riskScore: Float
    )
}

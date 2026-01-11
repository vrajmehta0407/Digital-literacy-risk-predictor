package com.dlrp.app.voice

/**
 * Voice warning templates for elderly-friendly alerts.
 * Provides simple, clear warnings in multiple languages.
 */
object VoiceWarningTemplates {
    
    /**
     * Get warning message based on risk level and language.
     */
    fun getWarning(riskLevel: String, language: String = "en"): String {
        return when (riskLevel.uppercase()) {
            "CRITICAL" -> getCriticalWarning(language)
            "DANGER" -> getDangerWarning(language)
            "WARNING" -> getWarningMessage(language)
            "SAFE" -> getSafeMessage(language)
            else -> getDefaultWarning(language)
        }
    }
    
    private fun getCriticalWarning(language: String): String {
        return when (language) {
            "hi" -> "गंभीर खतरा! यह स्कैम है। कुछ भी शेयर न करें। फोन काट दें।"
            "gu" -> "ગંભીર ખતરો! આ સ્કેમ છે। કંઈ શેર ન કરો। ફોન કાપો."
            else -> "Critical danger! This is a scam. Do not share anything. Hang up immediately."
        }
    }
    
    private fun getDangerWarning(language: String): String {
        return when (language) {
            "hi" -> "खतरा पाया गया! यह संदेश सुरक्षित नहीं है। कोई भी ओटीपी या पासवर्ड शेयर न करें।"
            "gu" -> "જોખમ મળ્યું! આ સંદેશ સુરક્ષિત નથી। કોઈ પણ OTP અથવા પાસવર્ડ શેર ન કરો."
            else -> "Danger detected! This message is not safe. Do not share any OTP or password."
        }
    }
    
    private fun getWarningMessage(language: String): String {
        return when (language) {
            "hi" -> "सावधान रहें! यह संदेश संदिग्ध है। कोई भी जानकारी शेयर करने से पहले सोचें।"
            "gu" -> "સાવધાન રહો! આ સંદેશ શંકાસ્પદ છે। કોઈ પણ માહિતી શેર કરતા પહેલા વિચારો."
            else -> "Be careful! This message is suspicious. Think before sharing any information."
        }
    }
    
    private fun getSafeMessage(language: String): String {
        return when (language) {
            "hi" -> "यह संदेश सुरक्षित लगता है।"
            "gu" -> "આ સંદેશ સુરક્ષિત લાગે છે."
            else -> "This message appears safe."
        }
    }
    
    private fun getDefaultWarning(language: String): String {
        return when (language) {
            "hi" -> "संदेश की जांच की गई है।"
            "gu" -> "સંદેશ તપાસવામાં આવ્યો છે."
            else -> "Message has been checked."
        }
    }
    
    /**
     * Get specific warning for call-OTP correlation.
     */
    fun getCallOTPWarning(language: String = "en"): String {
        return when (language) {
            "hi" -> "खतरा! अभी-अभी एक संदिग्ध कॉल आई थी और अब ओटीपी आया है। यह स्कैम हो सकता है। किसी को भी ओटीपी न बताएं!"
            "gu" -> "જોખમ! હમણાં જ એક શંકાસ્પદ કૉલ આવ્યો હતો અને હવે OTP આવ્યો છે. આ સ્કેમ હોઈ શકે છે. કોઈને પણ OTP ન કહો!"
            else -> "Danger! A suspicious call just came and now you received an OTP. This may be a scam. Do not share the OTP with anyone!"
        }
    }
    
    /**
     * Get warning for remote access detection.
     */
    fun getRemoteAccessWarning(language: String = "en"): String {
        return when (language) {
            "hi" -> "गंभीर खतरा! कोई आपके फोन को रिमोट एक्सेस करना चाहता है। यह स्कैम है। कोई भी ऐप इंस्टॉल न करें। फोन काट दें!"
            "gu" -> "ગંભીર ખતરો! કોઈ તમારા ફોનને રિમોટ એક્સેસ કરવા માંગે છે. આ સ્કેમ છે. કોઈ પણ એપ ઇન્સ્ટોલ ન કરો. ફોન કાપો!"
            else -> "Critical danger! Someone wants to remotely access your phone. This is a scam. Do not install any app. Hang up immediately!"
        }
    }
    
    /**
     * Get warning for fake bank detection.
     */
    fun getFakeBankWarning(bankName: String, language: String = "en"): String {
        return when (language) {
            "hi" -> "नकली बैंक चेतावनी! यह असली $bankName नहीं है। यह स्कैम है। कोई भी जानकारी शेयर न करें!"
            "gu" -> "નકલી બેંક ચેતવણી! આ વાસ્તવિક $bankName નથી. આ સ્કેમ છે. કોઈ પણ માહિતી શેર ન કરો!"
            else -> "Fake bank alert! This is NOT the real $bankName. This is a scam. Do not share any information!"
        }
    }
    
    /**
     * Get daily safety tip.
     */
    fun getDailySafetyTip(tipNumber: Int, language: String = "en"): String {
        val tips = when (language) {
            "hi" -> listOf(
                "याद रखें: बैंक कभी भी फोन पर ओटीपी नहीं मांगता।",
                "किसी भी लिंक पर क्लिक करने से पहले सोचें।",
                "अगर कोई जल्दी करने को कहे, तो सावधान रहें।",
                "अपना पासवर्ड किसी के साथ शेयर न करें।",
                "संदिग्ध मैसेज मिलने पर अपने परिवार को बताएं।"
            )
            "gu" -> listOf(
                "યાદ રાખો: બેંક ક્યારેય ફોન પર OTP માંગતી નથી.",
                "કોઈ પણ લિંક પર ક્લિક કરતા પહેલા વિચારો.",
                "જો કોઈ ઉતાવળ કરવા કહે, તો સાવધાન રહો.",
                "તમારો પાસવર્ડ કોઈ સાથે શેર ન કરો.",
                "શંકાસ્પદ સંદેશ મળે તો તમારા પરિવારને કહો."
            )
            else -> listOf(
                "Remember: Banks never ask for OTP over phone.",
                "Think before clicking any link.",
                "If someone rushes you, be careful.",
                "Never share your password with anyone.",
                "Tell your family if you receive suspicious messages."
            )
        }
        
        return tips[tipNumber % tips.size]
    }
}

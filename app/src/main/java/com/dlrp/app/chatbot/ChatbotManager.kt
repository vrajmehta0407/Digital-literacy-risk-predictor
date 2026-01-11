package com.dlrp.app.chatbot

/**
 * Simple chatbot with predefined responses for elderly users.
 */
class ChatbotManager(private val language: String = "en") {
    
    data class ChatMessage(
        val text: String,
        val isBot: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Get bot response for user input.
     */
    fun getResponse(userInput: String): String {
        val input = userInput.lowercase().trim()
        
        // Greetings
        if (input.contains("hello") || input.contains("hi") || input.contains("hey")) {
            return getGreeting()
        }
        
        // Help requests
        if (input.contains("help") || input.contains("how")) {
            return when {
                input.contains("block") -> getBlockingHelp()
                input.contains("guardian") -> getGuardianHelp()
                input.contains("premium") -> getPremiumHelp()
                input.contains("scam") || input.contains("suspicious") -> getScamHelp()
                else -> getGeneralHelp()
            }
        }
        
        // Premium questions
        if (input.contains("premium") || input.contains("upgrade") || input.contains("price")) {
            return getPremiumInfo()
        }
        
        // Scam reporting
        if (input.contains("scam") || input.contains("suspicious") || input.contains("fraud")) {
            return getScamReportHelp()
        }
        
        // Settings help
        if (input.contains("setting") || input.contains("language") || input.contains("voice")) {
            return getSettingsHelp()
        }
        
        // Emergency
        if (input.contains("emergency") || input.contains("urgent") || input.contains("danger")) {
            return getEmergencyHelp()
        }
        
        // Default response
        return getDefaultResponse()
    }
    
    private fun getGreeting(): String {
        return when (language) {
            "hi" -> "नमस्ते! मैं आपकी सुरक्षा सहायक हूं। मैं आपकी कैसे मदद कर सकती हूं?"
            "gu" -> "નમસ્તે! હું તમારી સુરક્ષા સહાયક છું. હું તમને કેવી રીતે મદદ કરી શકું?"
            else -> "Hello! I'm your safety assistant. How can I help you today?"
        }
    }
    
    private fun getGeneralHelp(): String {
        return """
            I can help you with:
            
            🛡️ Understanding scam protection
            📱 Blocking suspicious messages
            👨‍👩‍👧 Setting up guardians
            ⭐ Upgrading to Premium
            ⚙️ Changing settings
            🚨 Emergency assistance
            
            What would you like to know about?
        """.trimIndent()
    }
    
    private fun getBlockingHelp(): String {
        return """
            To block messages:
            
            1. The app automatically blocks dangerous scams
            2. View blocked messages: Menu → Scanned Messages
            3. You can see all blocked scams there
            
            The app protects you 24/7! 🛡️
        """.trimIndent()
    }
    
    private fun getGuardianHelp(): String {
        return """
            To set up your guardian:
            
            1. Tap Menu → Guardian Settings
            2. Enter guardian's phone number
            3. They'll get alerts when scams are detected
            
            Premium users can add up to 5 guardians! ⭐
        """.trimIndent()
    }
    
    private fun getPremiumHelp(): String {
        return """
            Premium Features (₹500/year):
            
            ⭐ Advanced AI scam detection
            ⭐ Automatic call blocking
            ⭐ Up to 5 guardians
            ⭐ Cloud backup
            ⭐ Call recording
            ⭐ Priority support
            
            Tap Menu → Premium to upgrade!
        """.trimIndent()
    }
    
    private fun getPremiumInfo(): String {
        return """
            Premium Subscription: ₹500/year
            
            ✅ 7-day FREE trial
            ✅ Advanced protection
            ✅ Multiple guardians
            ✅ Cloud backup
            ✅ Priority support
            
            Would you like to start your free trial?
        """.trimIndent()
    }
    
    private fun getScamHelp(): String {
        return """
            If you received a suspicious message:
            
            1. DON'T click any links
            2. DON'T share OTP codes
            3. DON'T call back unknown numbers
            4. Check Menu → Scanned Messages to see if it was blocked
            
            The app will alert you if it's dangerous! 🚨
        """.trimIndent()
    }
    
    private fun getScamReportHelp(): String {
        return """
            To report a scam:
            
            1. Take a screenshot of the message
            2. Go to Menu → Help & Support
            3. Contact your guardian
            4. Call Cyber Crime: 1930
            
            You're safe! The app blocked the scam. ✅
        """.trimIndent()
    }
    
    private fun getSettingsHelp(): String {
        return """
            To change settings:
            
            📱 Language: Menu → Language
            🔊 Voice: Menu → Voice Settings
            👨‍👩‍👧 Guardian: Menu → Guardian Settings
            
            What would you like to change?
        """.trimIndent()
    }
    
    private fun getEmergencyHelp(): String {
        return """
            🚨 EMERGENCY HELP:
            
            1. Tap the red Emergency button on dashboard
            2. This calls your guardian immediately
            3. They will be notified via SMS
            
            Emergency Numbers:
            Police: 100
            Cyber Crime: 1930
            
            Stay calm, you're protected! 🛡️
        """.trimIndent()
    }
    
    private fun getDefaultResponse(): String {
        return """
            I'm here to help! You can ask me about:
            
            • How to block scams
            • Setting up guardians
            • Premium features
            • Emergency help
            • App settings
            
            What would you like to know?
        """.trimIndent()
    }
    
    /**
     * Get quick reply suggestions.
     */
    fun getQuickReplies(): List<String> {
        return listOf(
            "How to block scams?",
            "What is Premium?",
            "Set up guardian",
            "Emergency help",
            "Change settings"
        )
    }
}

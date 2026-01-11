package com.dlrp.app.voice

import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceAlertManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isReady = false
    private var currentLanguage = "en"
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Load language from preferences
            currentLanguage = prefs.getString("language", "en") ?: "en"
            setLanguage(currentLanguage)
            
            // Load speech rate from preferences
            val speechRate = prefs.getFloat("speech_rate", 0.8f)
            tts.setSpeechRate(speechRate)
            
            isReady = true
        }
    }
    
    /**
     * Set language for voice alerts.
     */
    fun setLanguage(language: String) {
        currentLanguage = language
        val locale = when (language) {
            "hi" -> Locale("hi", "IN")
            "gu" -> Locale("gu", "IN")
            "ta" -> Locale("ta", "IN")
            "te" -> Locale("te", "IN")
            "bn" -> Locale("bn", "IN")
            else -> Locale.ENGLISH
        }
        
        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to English if language not supported
            tts.setLanguage(Locale.ENGLISH)
        }
    }

    /**
     * Speak warning message (selective reading - only the warning, not the scam content).
     */
    fun speakWarning(warningText: String) {
        // Reload language in case it changed
        val savedLanguage = prefs.getString("language", "en") ?: "en"
        if (savedLanguage != currentLanguage) {
            setLanguage(savedLanguage)
        }
        
        if (isReady) {
            tts.speak(warningText, TextToSpeech.QUEUE_FLUSH, null, "ALERT")
        }
    }
    
    /**
     * Speak warning based on risk level.
     */
    fun speakRiskWarning(riskLevel: String, language: String = "") {
        val lang = if (language.isEmpty()) {
            prefs.getString("language", "en") ?: "en"
        } else {
            language
        }
        
        setLanguage(lang)
        val warning = VoiceWarningTemplates.getWarning(riskLevel, lang)
        speakWarning(warning)
    }
    
    /**
     * Speak call-OTP correlation warning.
     */
    fun speakCallOTPWarning(language: String = "") {
        val lang = if (language.isEmpty()) {
            prefs.getString("language", "en") ?: "en"
        } else {
            language
        }
        
        setLanguage(lang)
        val warning = VoiceWarningTemplates.getCallOTPWarning(lang)
        speakWarning(warning)
    }
    
    /**
     * Speak remote access warning.
     */
    fun speakRemoteAccessWarning(language: String = "") {
        val lang = if (language.isEmpty()) {
            prefs.getString("language", "en") ?: "en"
        } else {
            language
        }
        
        setLanguage(lang)
        val warning = VoiceWarningTemplates.getRemoteAccessWarning(lang)
        speakWarning(warning)
    }
    
    /**
     * Speak fake bank warning.
     */
    fun speakFakeBankWarning(bankName: String, language: String = "") {
        val lang = if (language.isEmpty()) {
            prefs.getString("language", "en") ?: "en"
        } else {
            language
        }
        
        setLanguage(lang)
        val warning = VoiceWarningTemplates.getFakeBankWarning(bankName, lang)
        speakWarning(warning)
    }

    fun speak(text: String) {
        // Reload language in case it changed
        val savedLanguage = prefs.getString("language", "en") ?: "en"
        if (savedLanguage != currentLanguage) {
            setLanguage(savedLanguage)
        }
        
        if (isReady) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ALERT")
        }
    }
    
    fun stop() {
        if (isReady) {
            tts.stop()
        }
    }
    
    fun shutdown() {
        tts.shutdown()
    }
}

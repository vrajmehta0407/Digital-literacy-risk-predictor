package com.dlrp.app.voice

import java.util.Locale

object LanguageVoiceMapper {
    
    fun getLocaleForLanguage(langCode: String): Locale {
        return when(langCode) {
            "hi" -> Locale("hi", "IN")
            else -> Locale.ENGLISH
        }
    }
}

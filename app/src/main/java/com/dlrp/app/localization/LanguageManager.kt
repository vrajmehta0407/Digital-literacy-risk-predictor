package com.dlrp.app.localization

import java.util.Locale

object LanguageManager {
    
    var currentLocale: Locale = Locale.getDefault()

    fun setLanguage(languageCode: String) {
        currentLocale = Locale(languageCode)
        // Logic to update configuration would go here
    }
}

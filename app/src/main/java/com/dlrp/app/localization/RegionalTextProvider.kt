package com.dlrp.app.localization

import android.content.Context
import com.dlrp.app.R

class RegionalTextProvider(private val context: Context) {

    fun getSafeMessage(): String {
        // In real app, switch on LanguageManager.currentLocale
        return "You are Safe"
    }

    fun getDangerMessage(): String {
        return "Warning! Scam detected."
    }
}

package com.dlrp.app.core

import android.content.Context
import android.view.accessibility.AccessibilityManager

object AccessibilityHelper {

    fun isScreenReaderEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return am.isEnabled && am.isTouchExplorationEnabled
    }

    @Suppress("UNUSED_PARAMETER")
    fun announceForAccessibility(context: Context, text: String) {
        // This would interface with Accessibility events if needed
        // For now, relies on VoiceAlertManager
    }
}

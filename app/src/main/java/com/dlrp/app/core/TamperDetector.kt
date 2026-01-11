package com.dlrp.app.core

import android.content.Context
import android.content.pm.PackageManager
import com.dlrp.app.guardian.GuardianNotifier

/**
 * Detects tampering attempts and protects app integrity.
 * Warns guardian if someone tries to disable the app.
 */
class TamperDetector(
    private val context: Context,
    private val guardianNotifier: GuardianNotifier
) {
    
    companion object {
        private const val PREFS_NAME = "tamper_detection"
        private const val KEY_LAST_CHECK = "last_tamper_check"
        private const val KEY_PERMISSION_STATE = "permission_state"
    }
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Check for tampering attempts.
     */
    fun checkForTampering(): TamperResult {
        val currentTime = System.currentTimeMillis()
        val lastCheck = prefs.getLong(KEY_LAST_CHECK, currentTime)
        
        val tamperEvents = mutableListOf<String>()
        
        // Check if critical permissions were revoked
        if (checkPermissionRevocation()) {
            tamperEvents.add("Critical permissions were revoked")
            notifyGuardianOfTamper("Permission Revocation", 
                "Someone revoked critical app permissions")
        }
        
        // Check if app was recently disabled/enabled
        if (checkAppDisableAttempt()) {
            tamperEvents.add("App disable attempt detected")
            notifyGuardianOfTamper("App Disable Attempt", 
                "Someone tried to disable the app")
        }
        
        // Update last check time
        prefs.edit().putLong(KEY_LAST_CHECK, currentTime).apply()
        
        return TamperResult(
            tamperDetected = tamperEvents.isNotEmpty(),
            tamperEvents = tamperEvents,
            timestamp = currentTime
        )
    }
    
    /**
     * Check if critical permissions were revoked.
     */
    private fun checkPermissionRevocation(): Boolean {
        val criticalPermissions = listOf(
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.READ_PHONE_STATE
        )
        
        val currentState = criticalPermissions.map { permission ->
            context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }.joinToString(",")
        
        val previousState = prefs.getString(KEY_PERMISSION_STATE, currentState) ?: currentState
        
        if (currentState != previousState) {
            prefs.edit().putString(KEY_PERMISSION_STATE, currentState).apply()
            
            // Check if any permission was revoked
            val current = currentState.split(",").map { it.toBoolean() }
            val previous = previousState.split(",").map { it.toBoolean() }
            
            for (i in current.indices) {
                if (previous[i] && !current[i]) {
                    return true // Permission was revoked
                }
            }
        }
        
        return false
    }
    
    /**
     * Check for app disable attempts.
     */
    private fun checkAppDisableAttempt(): Boolean {
        // This would require more complex implementation
        // For now, return false
        return false
    }
    
    /**
     * Notify guardian of tampering.
     */
    private fun notifyGuardianOfTamper(type: String, details: String) {
        guardianNotifier.sendAlert(
            "⚠️ Tamper Alert: $type",
            "$details. Please check with your loved one to ensure their safety."
        )
    }
    
    /**
     * Require PIN for sensitive operations.
     */
    fun requirePinForSensitiveOperation(): Boolean {
        // This would integrate with a PIN system
        // For now, return true (PIN verified)
        return true
    }
    
    data class TamperResult(
        val tamperDetected: Boolean,
        val tamperEvents: List<String>,
        val timestamp: Long
    )
}

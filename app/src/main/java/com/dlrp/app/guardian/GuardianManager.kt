package com.dlrp.app.guardian

import android.content.Context
import android.telephony.SmsManager
import com.dlrp.app.storage.LocalPreferences

class GuardianManager(private val context: Context, private val prefs: LocalPreferences) {

    fun sendEmergencyAlert(reason: String) {
        if (!prefs.isGuardianEnabled) return
        
        val phone = prefs.guardianPhone
        if (phone.isNotEmpty()) {
            try {
                val smsManager = SmsManager.getDefault()
                val message = "DLRP Alert: User detected risk. Reason: $reason. Please check on them."
                smsManager.sendTextMessage(phone, null, message, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setGuardianContact(name: String, phone: String) {
        prefs.guardianName = name
        prefs.guardianPhone = phone
        prefs.isGuardianEnabled = true
        // Optionally save to preferences immediately if not handled by property delegation
    }

    fun getGuardianContact(): GuardianContact? {
        if (!prefs.isGuardianEnabled) return null
        return GuardianContact(prefs.guardianName, prefs.guardianPhone)
    }
}

data class GuardianContact(val name: String, val phoneNumber: String)

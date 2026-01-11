package com.dlrp.app.guardian

import android.content.Context
import com.dlrp.app.storage.LocalPreferences

// Wrapper around LocalPreferences for Guardian specific context
class GuardianPreferences(context: Context) {
    private val localPrefs = LocalPreferences(context)

    fun setGuardian(name: String, phone: String) {
        localPrefs.guardianName = name
        localPrefs.guardianPhone = phone
        localPrefs.isGuardianEnabled = true
    }

    fun getGuardianNumber(): String = localPrefs.guardianPhone
    
    fun getGuardianPhone(): String = localPrefs.guardianPhone
}

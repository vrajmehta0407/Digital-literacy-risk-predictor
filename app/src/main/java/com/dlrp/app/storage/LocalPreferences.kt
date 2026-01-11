package com.dlrp.app.storage

import android.content.Context
import android.content.SharedPreferences

class LocalPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("dlrp_prefs", Context.MODE_PRIVATE)

    var guardianName: String
        get() = prefs.getString("guardian_name", "") ?: ""
        set(value) = prefs.edit().putString("guardian_name", value).apply()

    var guardianPhone: String
        get() = prefs.getString("guardian_phone", "") ?: ""
        set(value) = prefs.edit().putString("guardian_phone", value).apply()

    var isGuardianEnabled: Boolean
        get() = prefs.getBoolean("guardian_enabled", false)
        set(value) = prefs.edit().putBoolean("guardian_enabled", value).apply()

    var lastScanTime: Long
        get() = prefs.getLong("last_scan", 0)
        set(value) = prefs.edit().putLong("last_scan", value).apply()
}

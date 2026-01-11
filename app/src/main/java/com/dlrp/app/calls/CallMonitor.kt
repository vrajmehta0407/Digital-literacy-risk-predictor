package com.dlrp.app.calls

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.dlrp.app.core.AppInitializer


class CallMonitor(private val context: Context) {

    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private val suspiciousCallDetector = SuspiciousCallDetector(context)
    private val silentCallHandler = SilentCallHandler(context)
    
    fun startMonitoring() {
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        phoneNumber?.let {
                            checkCallRisk(it)
                        }
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                         // Optional: Restore ringer when call ends
                         // silentCallHandler.restoreRinger() 
                    }
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun checkCallRisk(number: String) {
        if (suspiciousCallDetector.isSuspicious(number)) {
            // 1. Silence the ringer
            silentCallHandler.silenceRinger()
            
            // 2. Voice Warning
            val warning = "Warning. Incoming call from an unknown number. Be careful."
            AppInitializer.instance.voiceAlertManager.speak(warning)
            
            // 3. (Optional) Launch Overlay Activity here if we want a visual warning too
        }
    }
}

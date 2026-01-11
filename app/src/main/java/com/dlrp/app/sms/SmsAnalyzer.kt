package com.dlrp.app.sms

import android.content.Context
import android.content.Intent
import android.util.Log
import com.dlrp.app.core.AppInitializer
import com.dlrp.app.risk.RiskLevel
import com.dlrp.app.risk.RiskState
import com.dlrp.app.ui.HomeActivity
import com.dlrp.app.ui.PanicOverlayActivity

class SmsAnalyzer(private val context: Context) {

    companion object {
        private const val TAG = "SmsAnalyzer"
    }

    fun processMessage(sender: String, body: String): Boolean {
        Log.d(TAG, "Processing message from: $sender")
        Log.d(TAG, "Message body: ${body.take(50)}...")
        
        val riskState = AppInitializer.instance.scamRuleEngine.analyze(sender, body)
        
        Log.d(TAG, "Risk level: ${riskState.level}")
        Log.d(TAG, "Reasons: ${riskState.reasons}")

        // Only block DANGER level messages (high confidence scams)
        val shouldBlock = riskState.level == RiskLevel.DANGER
        
        if (riskState.level == RiskLevel.DANGER) {
            Log.d(TAG, "DANGER detected - will block and alert")
            // Trigger Alert (but don't block yet, receiver will block)
            triggerAlert(riskState, sender, body)
            
            // Log Event
            com.dlrp.app.storage.EventLogStore.logEvent(
                type = "SCAM_BLOCKED",
                details = "Scam BLOCKED from $sender. Risk: ${riskState.level}",
                riskLevel = riskState.level.toString()
            )
        } else if (riskState.level == RiskLevel.CAUTION) {
            Log.d(TAG, "CAUTION level - showing warning but NOT blocking")
            // Show warning but don't block
            triggerAlert(riskState, sender, body)
            
            com.dlrp.app.storage.EventLogStore.logEvent(
                type = "SCAM_WARNING",
                details = "Suspicious message from $sender. Risk: ${riskState.level}",
                riskLevel = riskState.level.toString()
            )
        } else {
            Log.d(TAG, "Message deemed safe, no alert triggered")
        }
        
        return shouldBlock
    }

    private fun triggerAlert(riskState: RiskState, sender: String, messageBody: String) {
        val level = riskState.level
        val extractedCode = riskState.extractedCode

        Log.d(TAG, "triggerAlert called - Level: $level, Has code: ${extractedCode != null}")

        if (level == RiskLevel.DANGER) {
            
            if (extractedCode != null) {
                Log.d(TAG, "Launching SafetyAlertActivity with OTP: ${extractedCode.take(2)}***")
                // Launch Security Overlay for OTP
                com.dlrp.app.ui.SafetyAlertActivity.launch(context, extractedCode, sender, messageBody)
            } else {
                Log.d(TAG, "Launching PanicOverlayActivity (no OTP found)")
                // Launch Generic Panic Overlay
                val intent = Intent(context, PanicOverlayActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("RISK_LEVEL", "DANGER")
                    putExtra("SENDER", sender)
                }
                context.startActivity(intent)
            }
            
            // Speak Warning
            val warningMessage = "Warning, Danger detected from $sender. Do not share any code."
            Log.d(TAG, "Speaking warning: $warningMessage")
            AppInitializer.instance.voiceAlertManager.speak(warningMessage)
        } else {
            Log.d(TAG, "Caution level - speaking caution message")
             // Caution (maybe notification or update UI)
             AppInitializer.instance.voiceAlertManager.speak("Be careful, suspicious message from $sender.")
        }
    }
}

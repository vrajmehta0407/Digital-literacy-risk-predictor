package com.dlrp.app.guardian

import android.content.Context
import com.dlrp.app.core.AppInitializer

class GuardianNotifier(private val context: Context) {

    fun notifyRisk(riskType: String, details: String) {
        AppInitializer.instance.guardianManager.sendEmergencyAlert("Risk Detected: $riskType. $details")
    }
    
    /**
     * Send general alert to guardian.
     */
    fun sendAlert(title: String, message: String) {
        AppInitializer.instance.guardianManager.sendEmergencyAlert("$title\n$message")
    }
    
    /**
     * Send weekly summary to guardian.
     */
    fun sendWeeklySummary(summary: String) {
        AppInitializer.instance.guardianManager.sendEmergencyAlert(summary)
    }
    
    /**
     * Send contact approval request to guardian.
     */
    fun sendContactApprovalRequest(phoneNumber: String, messagePreview: String) {
        val message = "Contact Approval Needed\n\n" +
                     "New contact: $phoneNumber\n" +
                     "Message preview: $messagePreview\n\n" +
                     "Please approve or reject this contact."
        AppInitializer.instance.guardianManager.sendEmergencyAlert(message)
    }
    
    /**
     * Send emergency call notification.
     */
    fun sendEmergencyCallNotification(reason: String) {
        AppInitializer.instance.guardianManager.sendEmergencyAlert(
            "Emergency Call Triggered\n\nReason: $reason"
        )
    }
}

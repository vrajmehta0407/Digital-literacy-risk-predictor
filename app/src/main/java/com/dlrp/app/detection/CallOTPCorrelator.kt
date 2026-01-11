package com.dlrp.app.detection

import com.dlrp.app.calls.CallContextStore
import com.dlrp.app.storage.EventLogStore

/**
 * Correlates suspicious calls with OTP/message arrivals to detect scam attempts.
 * If a scam call happens and an OTP arrives within a few minutes, automatically
 * raises the danger level and triggers voice warnings.
 */
class CallOTPCorrelator {
    
    companion object {
        private const val CORRELATION_WINDOW_MS = 5 * 60 * 1000L // 5 minutes
        private const val OTP_PATTERN = "\\b\\d{4,6}\\b" // 4-6 digit OTP
    }
    
    /**
     * Check if there's a suspicious call-OTP correlation.
     * @param sender The message sender
     * @param messageBody The message content
     * @return True if correlation detected (scam call + OTP within time window)
     */
    fun hasCallOTPCorrelation(sender: String, messageBody: String): Boolean {
        // Check if message contains OTP
        if (!containsOTP(messageBody)) {
            return false
        }
        
        // Check if there was a recent suspicious call
        return CallContextStore.isCallRecentlySuspicious(
            sender, 
            windowMillis = CORRELATION_WINDOW_MS
        )
    }
    
    /**
     * Check if message contains OTP pattern.
     */
    private fun containsOTP(message: String): Boolean {
        val otpRegex = Regex(OTP_PATTERN)
        return otpRegex.containsMatchIn(message)
    }
    
    /**
     * Get correlation details for logging/display.
     */
    fun getCorrelationDetails(sender: String): String {
        return "⚠️ DANGER: Suspicious call from this number was received recently. " +
               "This may be a scam attempt to steal your OTP!"
    }
    
    /**
     * Log the correlation event for guardian reporting.
     */
    fun logCorrelationEvent(sender: String, messageBody: String) {
        EventLogStore.logEvent(
            type = "CALL_OTP_CORRELATION",
            details = "Suspicious call followed by OTP from $sender",
            riskLevel = "DANGER"
        )
    }
}

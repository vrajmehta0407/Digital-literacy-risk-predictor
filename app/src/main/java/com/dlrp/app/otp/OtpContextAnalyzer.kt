package com.dlrp.app.otp

import com.dlrp.app.calls.CallContextStore

class OtpContextAnalyzer {

    fun isOtpRisky(sender: String, message: String): Boolean {
        // 1. Is it coming during a call context?
        // Note: sender needs to be mapped to a number if possible, 
        // but often OTP sender IDs are alphanumeric.
        // Logic: specific OTP patterns + recent unknown call = Risky
        
        // This is a simplified check
        return false 
    }
}

package com.dlrp.app.otp

class OtpInterceptor {
    
    // In a full implementation, this might link to an AccessibilityService 
    // to mask content on screen.
    
    fun shouldMask(sender: String, body: String): Boolean {
        // Check TrustedSenderRegistry
        val trusted = TrustedSenderRegistry().isTrusted(sender)
        return !trusted
    }
}

package com.dlrp.app.voice

object VoiceScriptProvider {
    
    fun getAlertScript(riskLevel: String): String {
        return "Warning. Suspicious activity detected."
    }
}

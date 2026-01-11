package com.dlrp.app.otp

class TrustedSenderRegistry {
    
    // In a real app, this might query Contacts or a remote whitelist
    private val WHITELIST = setOf(
        "BANK", "GOV", "JD-KOTAK", "VM-HDFC", "Google", "Facebook" 
        // Examples of sender IDs
    )

    fun isTrusted(sender: String): Boolean {
        // Simple check: is in contacts? (Not implemented here, assumed false for strictness)
        // Or matches known alphanumeric branding
        return WHITELIST.any { sender.contains(it, ignoreCase = true) }
    }
}

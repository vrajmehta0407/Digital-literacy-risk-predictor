package com.dlrp.app.calls

import android.content.Context
import com.dlrp.app.storage.TrustedContactsStore

class SuspiciousCallDetector(private val context: Context) {

    private val trustedContactsStore = TrustedContactsStore(context)

    fun isSuspicious(number: String?): Boolean {
        if (number.isNullOrEmpty()) return true // Private numbers are suspicious
        
        // If in contacts, it is NOT suspicious
        if (trustedContactsStore.isTrusted(number)) {
            return false
        }

        // Logic for unknown numbers
        // In a real app, this would check spam lists.
        // For this safety-first model, unknown = potential risk.
        return true
    }
}

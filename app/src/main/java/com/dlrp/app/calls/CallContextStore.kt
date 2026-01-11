package com.dlrp.app.calls

import java.util.concurrent.ConcurrentHashMap

object CallContextStore {
    // Store recent unknown calls with timestamp
    // Key: PhoneNumber, Value: Timestamp
    private val recentSuspiciousCalls = ConcurrentHashMap<String, Long>()

    fun addSuspiciousCall(number: String) {
        recentSuspiciousCalls[number] = System.currentTimeMillis()
    }

    fun isCallRecentlySuspicious(number: String, windowMillis: Long = 5 * 60 * 1000): Boolean {
        val time = recentSuspiciousCalls[number] ?: return false
        return (System.currentTimeMillis() - time) < windowMillis
    }

    fun clear() {
        recentSuspiciousCalls.clear()
    }
}

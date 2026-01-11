package com.dlrp.app.storage

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class EventLogStore(private val context: Context) {

    private val logFile = File(context.filesDir, "risk_events.log")

    fun logEvent(riskLevel: String, sender: String, reason: String) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            val entry = "$timestamp | $riskLevel | $sender | $reason\n"
            FileWriter(logFile, true).use { it.write(entry) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun getLogs(): List<String> {
        if (!logFile.exists()) return emptyList()
        return logFile.readLines()
    }
    
    companion object {
        /**
         * Log event with type and details (static method for convenience).
         */
        fun logEvent(type: String, details: String, riskLevel: String) {
            android.util.Log.d("EventLogStore", "Event: $type | $riskLevel | $details")
            // In production, this would write to a database or file
        }
        
        /**
         * Get events for the last N days.
         */
        fun getEventsForLastDays(days: Int): List<EventLog> {
            // Mock implementation - in production, query from database
            return emptyList()
        }
    }
    
    data class EventLog(
        val timestamp: Long,
        val type: String,
        val details: String,
        val riskLevel: String
    )
}

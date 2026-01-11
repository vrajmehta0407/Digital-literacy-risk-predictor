package com.dlrp.app.guardian

import android.content.Context
import com.dlrp.app.storage.EventLogStore
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generates weekly activity summaries for guardians.
 * Provides simple, readable reports of scam protection activity.
 */
class WeeklySummaryGenerator(
    private val context: Context,
    private val guardianNotifier: GuardianNotifier
) {
    
    companion object {
        private const val DAYS_IN_WEEK = 7
    }
    
    /**
     * Generate and send weekly summary to guardian.
     */
    fun generateAndSendSummary() {
        val summary = generateSummary()
        sendSummaryToGuardian(summary)
    }
    
    /**
     * Generate weekly summary.
     */
    fun generateSummary(): WeeklySummary {
        val events = EventLogStore.getEventsForLastDays(DAYS_IN_WEEK)
        
        val scamMessagesBlocked = events.count { it.type == "SCAM_DETECTED" }
        val suspiciousCalls = events.count { it.type == "SUSPICIOUS_CALL" }
        val callOTPCorrelations = events.count { it.type == "CALL_OTP_CORRELATION" }
        val remoteAccessAttempts = events.count { it.type == "REMOTE_ACCESS_DETECTED" }
        val repeatedScams = events.count { it.type == "REPEATED_SCAM_ATTEMPT" }
        val fakeBankDetections = events.count { it.type == "FAKE_BANK_DETECTED" }
        
        val criticalEvents = events.filter { it.riskLevel == "CRITICAL" || it.riskLevel == "DANGER" }
        
        return WeeklySummary(
            weekStartDate = getWeekStartDate(),
            weekEndDate = getWeekEndDate(),
            scamMessagesBlocked = scamMessagesBlocked,
            suspiciousCalls = suspiciousCalls,
            callOTPCorrelations = callOTPCorrelations,
            remoteAccessAttempts = remoteAccessAttempts,
            repeatedScams = repeatedScams,
            fakeBankDetections = fakeBankDetections,
            criticalEvents = criticalEvents.size,
            totalEvents = events.size
        )
    }
    
    /**
     * Send summary to guardian.
     */
    private fun sendSummaryToGuardian(summary: WeeklySummary) {
        val message = formatSummaryMessage(summary)
        guardianNotifier.sendWeeklySummary(message)
    }
    
    /**
     * Format summary into readable message.
     */
    private fun formatSummaryMessage(summary: WeeklySummary): String {
        val sb = StringBuilder()
        sb.append("📊 Weekly Safety Report\n")
        sb.append("${summary.weekStartDate} to ${summary.weekEndDate}\n\n")
        
        if (summary.totalEvents == 0) {
            sb.append("✅ Great news! No scam attempts detected this week.\n")
        } else {
            sb.append("🛡️ Protection Summary:\n")
            
            if (summary.scamMessagesBlocked > 0) {
                sb.append("• ${summary.scamMessagesBlocked} scam message(s) blocked\n")
            }
            
            if (summary.suspiciousCalls > 0) {
                sb.append("• ${summary.suspiciousCalls} suspicious call(s) detected\n")
            }
            
            if (summary.callOTPCorrelations > 0) {
                sb.append("• ${summary.callOTPCorrelations} scam call + OTP attempt(s)\n")
            }
            
            if (summary.remoteAccessAttempts > 0) {
                sb.append("• ${summary.remoteAccessAttempts} remote access attempt(s) blocked\n")
            }
            
            if (summary.repeatedScams > 0) {
                sb.append("• ${summary.repeatedScams} repeated scam attempt(s)\n")
            }
            
            if (summary.fakeBankDetections > 0) {
                sb.append("• ${summary.fakeBankDetections} fake bank message(s) detected\n")
            }
            
            if (summary.criticalEvents > 0) {
                sb.append("\n⚠️ ${summary.criticalEvents} critical threat(s) blocked\n")
            }
        }
        
        sb.append("\nYour loved one is protected by Safe Senior app.")
        
        return sb.toString()
    }
    
    private fun getWeekStartDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -DAYS_IN_WEEK)
        return SimpleDateFormat("MMM dd", Locale.getDefault()).format(calendar.time)
    }
    
    private fun getWeekEndDate(): String {
        val calendar = Calendar.getInstance()
        return SimpleDateFormat("MMM dd", Locale.getDefault()).format(calendar.time)
    }
    
    data class WeeklySummary(
        val weekStartDate: String,
        val weekEndDate: String,
        val scamMessagesBlocked: Int,
        val suspiciousCalls: Int,
        val callOTPCorrelations: Int,
        val remoteAccessAttempts: Int,
        val repeatedScams: Int,
        val fakeBankDetections: Int,
        val criticalEvents: Int,
        val totalEvents: Int
    )
}

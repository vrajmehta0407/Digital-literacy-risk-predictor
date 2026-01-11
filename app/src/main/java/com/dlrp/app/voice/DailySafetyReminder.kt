package com.dlrp.app.voice

import android.content.Context
import android.content.SharedPreferences
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Manages daily safety reminders for elderly users.
 * Provides one simple spoken tip per day (can be disabled).
 */
class DailySafetyReminder(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "daily_reminders", Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_ENABLED = "reminders_enabled"
        private const val KEY_REMINDER_TIME_HOUR = "reminder_hour"
        private const val KEY_REMINDER_TIME_MINUTE = "reminder_minute"
        private const val KEY_LAST_TIP_INDEX = "last_tip_index"
        private const val KEY_LANGUAGE = "reminder_language"
        private const val DEFAULT_HOUR = 10 // 10 AM
        private const val DEFAULT_MINUTE = 0
    }
    
    /**
     * Enable or disable daily reminders.
     */
    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
        if (enabled) {
            scheduleDailyReminder()
        } else {
            cancelDailyReminder()
        }
    }
    
    /**
     * Check if reminders are enabled.
     */
    fun isEnabled(): Boolean {
        return prefs.getBoolean(KEY_ENABLED, false)
    }
    
    /**
     * Set reminder time.
     */
    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(KEY_REMINDER_TIME_HOUR, hour)
            .putInt(KEY_REMINDER_TIME_MINUTE, minute)
            .apply()
        
        if (isEnabled()) {
            scheduleDailyReminder()
        }
    }
    
    /**
     * Set reminder language.
     */
    fun setLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }
    
    /**
     * Get next safety tip.
     */
    fun getNextTip(): String {
        val language = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        val lastIndex = prefs.getInt(KEY_LAST_TIP_INDEX, -1)
        val nextIndex = lastIndex + 1
        
        // Save next index
        prefs.edit().putInt(KEY_LAST_TIP_INDEX, nextIndex).apply()
        
        return VoiceWarningTemplates.getDailySafetyTip(nextIndex, language)
    }
    
    /**
     * Schedule daily reminder using WorkManager.
     */
    private fun scheduleDailyReminder() {
        val hour = prefs.getInt(KEY_REMINDER_TIME_HOUR, DEFAULT_HOUR)
        val minute = prefs.getInt(KEY_REMINDER_TIME_MINUTE, DEFAULT_MINUTE)
        
        // Calculate initial delay
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        
        if (targetTime.before(currentTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis
        
        // Create periodic work request
        val reminderWork = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("daily_safety_reminder")
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_safety_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderWork
        )
    }
    
    /**
     * Cancel daily reminder.
     */
    private fun cancelDailyReminder() {
        WorkManager.getInstance(context).cancelUniqueWork("daily_safety_reminder")
    }
}

/**
 * Worker for daily safety reminders.
 */
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        val reminder = DailySafetyReminder(applicationContext)
        
        if (!reminder.isEnabled()) {
            return Result.success()
        }
        
        // Get next tip
        val tip = reminder.getNextTip()
        
        // Speak the tip
        val voiceAlertManager = VoiceAlertManager(applicationContext)
        voiceAlertManager.speakWarning(tip)
        
        return Result.success()
    }
}

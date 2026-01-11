package com.dlrp.app.core

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Manages adaptive scanning to minimize battery usage.
 * Adjusts scanning frequency based on battery level.
 */
@Suppress("UNUSED")
class AdaptiveScanningManager(private val context: Context) {
    
    companion object {
        private const val LOW_BATTERY_THRESHOLD = 20
        private const val NORMAL_SCAN_INTERVAL_MINUTES = 15L
        private const val LOW_BATTERY_SCAN_INTERVAL_MINUTES = 30L
    }
    
    /**
     * Get current battery level.
     */
    private fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
    
    /**
     * Check if battery is low.
     */
    fun isBatteryLow(): Boolean {
        return getBatteryLevel() <= LOW_BATTERY_THRESHOLD
    }
    
    /**
     * Check if the device is currently charging.
     */
    private fun isDeviceCharging(): Boolean {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    }
    
    /**
     * Get appropriate scan interval based on battery level.
     */
    fun getScanInterval(): Long {
        return if (isDeviceCharging() || !isBatteryLow()) {
            NORMAL_SCAN_INTERVAL_MINUTES
        } else {
            LOW_BATTERY_SCAN_INTERVAL_MINUTES
        }
    }
    
    /**
     * Schedule adaptive background scanning.
     */
    fun scheduleAdaptiveScanning() {
        val scanInterval = getScanInterval()
        
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false) // We handle battery ourselves
            .build()
        
        val scanWork = PeriodicWorkRequestBuilder<AdaptiveScanWorker>(
            scanInterval, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("adaptive_scanning")
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "adaptive_scanning",
            ExistingPeriodicWorkPolicy.REPLACE,
            scanWork
        )
    }
    
    /**
     * Get battery usage statistics.
     */
    fun getBatteryImpactInfo(): BatteryImpactInfo {
        val batteryLevel = getBatteryLevel()
        val scanInterval = getScanInterval()
        
        val estimatedDailyImpact = when {
            isDeviceCharging() -> 1.0f // Minimal impact when charging
            batteryLevel <= LOW_BATTERY_THRESHOLD -> 2.0f // 2% per day in low battery mode
            else -> 4.0f // 4% per day in normal mode
        }
        
        return BatteryImpactInfo(
            currentBatteryLevel = batteryLevel,
            isLowBatteryMode = isBatteryLow(),
            scanIntervalMinutes = scanInterval,
            estimatedDailyImpact = estimatedDailyImpact,
            isCharging = isDeviceCharging()
        )
    }
    
    data class BatteryImpactInfo(
        val currentBatteryLevel: Int,
        val isLowBatteryMode: Boolean,
        val scanIntervalMinutes: Long,
        val estimatedDailyImpact: Float,
        val isCharging: Boolean
    )
}

/**
 * Worker for adaptive background scanning.
 */
class AdaptiveScanWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        val scanningManager = AdaptiveScanningManager(applicationContext)
        
        // Adjust scan interval based on current battery
        scanningManager.scheduleAdaptiveScanning()
        
        // Perform lightweight background checks
        // (This would integrate with existing scam detection)
        
        return Result.success()
    }
}

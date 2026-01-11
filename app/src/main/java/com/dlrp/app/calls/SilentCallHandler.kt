package com.dlrp.app.calls

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import com.dlrp.app.core.AppInitializer

class SilentCallHandler(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var previousRingerMode: Int = AudioManager.RINGER_MODE_NORMAL

    fun silenceRinger() {
        try {
            // Save current mode to restore later (optional, or just leave silent until user interacts)
            previousRingerMode = audioManager.ringerMode

            // Check DND permission for Android M+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (notificationManager.isNotificationPolicyAccessGranted) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                } else {
                    // Fallback: just lower volume to 0 if we can't fully silence without DND permission
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                }
            } else {
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun restoreRinger() {
        try {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (notificationManager.isNotificationPolicyAccessGranted) {
                    audioManager.ringerMode = previousRingerMode
                }
            } else {
                audioManager.ringerMode = previousRingerMode
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

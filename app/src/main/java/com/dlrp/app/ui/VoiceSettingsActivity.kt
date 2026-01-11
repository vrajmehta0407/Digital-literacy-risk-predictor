package com.dlrp.app.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R
import com.dlrp.app.voice.VoiceAlertManager

/**
 * Voice settings for adjusting speech rate and enabling/disabling voice alerts.
 */
class VoiceSettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var voiceAlertManager: VoiceAlertManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_settings)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Voice Settings"
        
        prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        voiceAlertManager = VoiceAlertManager(this)
        
        setupVoiceSettings()
    }
    
    private fun setupVoiceSettings() {
        val switchVoiceEnabled = findViewById<Switch>(R.id.switchVoiceEnabled)
        val seekBarSpeed = findViewById<SeekBar>(R.id.seekBarSpeed)
        val tvSpeedValue = findViewById<TextView>(R.id.tvSpeedValue)
        val btnTestVoice = findViewById<Button>(R.id.btnTestVoice)
        
        // Load current settings
        val voiceEnabled = prefs.getBoolean("voice_enabled", true)
        val speechRate = prefs.getFloat("speech_rate", 0.8f)
        
        switchVoiceEnabled.isChecked = voiceEnabled
        seekBarSpeed.progress = ((speechRate - 0.5f) * 100).toInt()
        tvSpeedValue.text = String.format("%.1fx", speechRate)
        
        // Voice enable/disable
        switchVoiceEnabled.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("voice_enabled", isChecked).apply()
        }
        
        // Speech rate adjustment
        seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rate = 0.5f + (progress / 100f)
                tvSpeedValue.text = String.format("%.1fx", rate)
                prefs.edit().putFloat("speech_rate", rate).apply()
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Test voice
        btnTestVoice.setOnClickListener {
            val testMessage = "This is a test of the voice alert system. Banks never ask for OTP over phone."
            voiceAlertManager.speakWarning(testMessage)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

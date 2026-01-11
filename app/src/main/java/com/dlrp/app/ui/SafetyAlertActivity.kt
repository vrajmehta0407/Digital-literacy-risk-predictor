package com.dlrp.app.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R
import com.dlrp.app.core.AppInitializer
import com.dlrp.app.voice.VoiceWarningTemplates

class SafetyAlertActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_OTP = "extra_otp"
        private const val EXTRA_SENDER = "extra_sender"
        private const val EXTRA_MESSAGE_BODY = "extra_message_body"

        fun launch(context: Context, otp: String, sender: String, messageBody: String = "") {
            val intent = Intent(context, SafetyAlertActivity::class.java).apply {
                putExtra(EXTRA_OTP, otp)
                putExtra(EXTRA_SENDER, sender)
                putExtra(EXTRA_MESSAGE_BODY, messageBody)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SECURE FLAG: Prevents Screenshots and Screen Recording
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_safety_alert)
        
        val otp = intent.getStringExtra(EXTRA_OTP) ?: ""
        val sender = intent.getStringExtra(EXTRA_SENDER) ?: "Unknown"
        val messageBody = intent.getStringExtra(EXTRA_MESSAGE_BODY) ?: ""

        // Display sender information
        findViewById<TextView>(R.id.tvSenderInfo)?.apply {
            text = "⚠️ Suspicious message from:\n$sender"
            textSize = 18f
            setTextColor(getColor(android.R.color.white))
        }

        // Display masked OTP
        findViewById<TextView>(R.id.tvMaskedCode)?.apply {
            text = "* ".repeat(otp.length).trim()
        }
        
        // Display warning message
        findViewById<TextView>(R.id.tvWarningMessage)?.apply {
            text = "🚨 DANGER DETECTED!\n\n" +
                   "This message contains a security code (OTP).\n\n" +
                   "⛔ NEVER share this code with anyone\n" +
                   "⛔ Banks NEVER ask for OTP over phone\n" +
                   "⛔ This could be a SCAM attempt\n\n" +
                   "If you shared this code, contact your bank IMMEDIATELY!"
            textSize = 16f
            setTextColor(getColor(android.R.color.white))
        }

        // Play Enhanced Voice Warning
        val language = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .getString("language", "en") ?: "en"
        
        val voiceWarning = when (language) {
            "hi" -> "चेतावनी! यह कोड किसी के साथ साझा न करें। बैंक कभी भी फोन पर OTP नहीं मांगते।"
            "gu" -> "ચેતવણી! આ કોડ કોઈની સાથે શેર ન કરો। બેંક ક્યારેય ફોન પર OTP માંગતી નથી।"
            else -> "Warning! Do not share this code with anyone. Banks never ask for OTP over phone."
        }
        AppInitializer.instance.voiceAlertManager.speakWarning(voiceWarning)

        // Log the event
        com.dlrp.app.storage.EventLogStore.logEvent(
            type = "SCAM_DETECTED_OTP",
            details = "OTP scam detected from $sender. Code: ${otp.take(2)}***",
            riskLevel = "DANGER"
        )

        // Large "I Did NOT Share" Confirmation Button
        findViewById<Button>(R.id.btnConfirmNotShared)?.apply {
            text = "✓ I Did NOT Share This Code"
            textSize = 20f
            setBackgroundColor(getColor(android.R.color.holo_green_dark))
            setTextColor(getColor(android.R.color.white))
            setPadding(32, 32, 32, 32)
            setOnClickListener {
                // Log confirmation
                logUserConfirmation(sender, otp)
                // Show reassurance message
                showReassuranceMessage()
            }
        }
        
        // Close Button
        findViewById<Button>(R.id.btnDismiss).setOnClickListener {
            AppInitializer.instance.voiceAlertManager.stop()
            finish()
        }
    }

    override fun onBackPressed() {
        // Prevent accidental closing, force user to click the button
        // User must acknowledge the warning
    }
    
    private fun logUserConfirmation(sender: String, otp: String) {
        com.dlrp.app.storage.EventLogStore.logEvent(
            type = "USER_CONFIRMED_NOT_SHARED",
            details = "User confirmed they did not share OTP from $sender",
            riskLevel = "INFO"
        )
        
        // Notify guardian
        val guardianNotifier = com.dlrp.app.guardian.GuardianNotifier(this)
        guardianNotifier.sendAlert(
            "OTP Scam Blocked",
            "User received OTP scam from $sender but confirmed they did NOT share the code. Good job!"
        )
    }
    
    private fun showReassuranceMessage() {
        android.app.AlertDialog.Builder(this)
            .setTitle("✓ Excellent!")
            .setMessage(
                "You made the RIGHT choice!\n\n" +
                "✅ You are SAFE\n" +
                "✅ Your money is PROTECTED\n" +
                "✅ The scammer FAILED\n\n" +
                "Always remember: NEVER share OTP codes with anyone!"
            )
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        AppInitializer.instance.voiceAlertManager.stop()
    }
}

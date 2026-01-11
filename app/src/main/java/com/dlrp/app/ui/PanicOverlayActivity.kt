package com.dlrp.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R
import com.dlrp.app.core.AppInitializer
import com.dlrp.app.guardian.GuardianPreferences

class PanicOverlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panic_overlay)

        val riskLevel = intent.getStringExtra("RISK_LEVEL") ?: "DANGER"
        val sender = intent.getStringExtra("SENDER") ?: "Unknown"

        val tvWarning = findViewById<TextView>(R.id.tvWarning)
        val btnClose = findViewById<Button>(R.id.btnClose)
        val btnCallGuardian = findViewById<Button>(R.id.btnCallGuardian)

        tvWarning.text = "WARNING\n\nSuspicious message from $sender.\n\nDO NOT SHARE CODES."
        
        // Emergency Call Guardian Button
        btnCallGuardian?.apply {
            text = "📞 Call Guardian"
            textSize = 18f
            setBackgroundColor(getColor(android.R.color.holo_orange_dark))
            setTextColor(getColor(android.R.color.white))
            setOnClickListener {
                callGuardian()
            }
        }
        
        // Block back button or minimize distractions (simulated here)
        
        btnClose.setOnClickListener {
            // Log acceptance?
            finish()
        }
        
        // Notify Guardian Automatically on Danger
        if (riskLevel == "DANGER") {
            AppInitializer.instance.guardianManager.sendEmergencyAlert("High Risk Detected from $sender")
        }
    }
    
    private fun callGuardian() {
        val guardianPrefs = GuardianPreferences(this)
        val guardianPhone = guardianPrefs.getGuardianPhone()
        
        if (guardianPhone.isNotEmpty()) {
            // Log emergency call
            com.dlrp.app.storage.EventLogStore.logEvent(
                type = "EMERGENCY_CALL_INITIATED",
                details = "User initiated emergency call to guardian",
                riskLevel = "CRITICAL"
            )
            
            // Notify guardian via SMS first
            val guardianNotifier = com.dlrp.app.guardian.GuardianNotifier(this)
            guardianNotifier.sendEmergencyCallNotification("User pressed emergency call button")
            
            // Initiate phone call
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$guardianPhone")
            }
            startActivity(callIntent)
        } else {
            // Show error - no guardian configured
            android.app.AlertDialog.Builder(this)
                .setTitle("No Guardian Configured")
                .setMessage("Please set up a guardian contact in settings first.")
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    override fun onBackPressed() {
        // Disable back button during Panic/Danger to force user to read
        // super.onBackPressed()
    }
}

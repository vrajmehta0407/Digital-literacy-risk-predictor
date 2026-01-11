package com.dlrp.app.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R

/**
 * Tutorial activity showing how to use the app.
 */
class TutorialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Tutorial"
        
        loadTutorial()
    }
    
    private fun loadTutorial() {
        val tutorialText = """
            📚 How to Use Safe Senior
            
            🛡️ PROTECTION
            • The app automatically scans all incoming messages and calls
            • Dangerous messages are blocked and you'll be alerted
            • Your guardian will be notified of critical threats
            
            🚨 EMERGENCY
            • Tap the red Emergency Call button anytime
            • This will call your guardian immediately
            • Your guardian will receive an SMS notification
            
            💡 DAILY TIPS
            • Read daily safety tips to stay informed
            • Tips are available in multiple languages
            • New tip every day!
            
            👨‍👩‍👧 GUARDIAN SETUP
            • Add your guardian's phone number in settings
            • They will receive alerts about threats
            • They can help you stay safe
            
            🌐 LANGUAGE
            • Choose your preferred language
            • Available: English, Hindi, Gujarati, Tamil, Telugu, Bengali
            • Voice alerts will use your language
            
            🔊 VOICE ALERTS
            • The app can speak warnings out loud
            • Adjust speech speed in Voice Settings
            • Enable/disable as needed
            
            📊 PROTECTION STATUS
            • View how many scams were blocked
            • See your protection history
            • Check critical threats
            
            🔒 PRIVACY
            • All scanning happens on your device
            • No data sent to external servers
            • Your privacy is protected
            
            ❓ NEED HELP?
            • Contact your guardian
            • Check Help & FAQ section
            • Call emergency services if needed
        """.trimIndent()
        
        findViewById<TextView>(R.id.tvTutorialContent).text = tutorialText
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

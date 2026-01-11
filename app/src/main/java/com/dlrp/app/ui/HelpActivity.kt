package com.dlrp.app.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R

/**
 * Help & FAQ activity.
 */
class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Help & FAQ"
        
        loadHelp()
    }
    
    private fun loadHelp() {
        val helpText = """
            ❓ Frequently Asked Questions
            
            Q: How does the app protect me?
            A: The app automatically scans all incoming messages and calls for scam patterns. It uses AI to detect fraud attempts and alerts you immediately.
            
            Q: Will the app work without internet?
            A: Yes! All scam detection happens on your device. No internet required for protection.
            
            Q: What happens when a scam is detected?
            A: You'll see a large warning screen, hear a voice alert, and your guardian will be notified via SMS.
            
            Q: How do I set up my guardian?
            A: Go to Menu → Guardian Settings and enter their phone number.
            
            Q: Can I change the language?
            A: Yes! Go to Menu → Language and select from 6 languages: English, Hindi, Gujarati, Tamil, Telugu, Bengali.
            
            Q: How do I adjust voice speed?
            A: Go to Menu → Voice Settings and use the slider to adjust speech speed.
            
            Q: What if I accidentally share information?
            A: Immediately press the "I Did NOT Share" button on the warning screen. This will alert your guardian.
            
            Q: Is my data safe?
            A: Absolutely! All data stays on your device. Nothing is sent to external servers.
            
            Q: How do I call my guardian quickly?
            A: Tap the red Emergency Call button on the dashboard or use the panic button.
            
            Q: What are the daily safety tips?
            A: These are educational tips to help you recognize scams. A new tip appears each day.
            
            Q: Can I see my protection history?
            A: Yes! Go to Menu → Protection Status to see statistics and history.
            
            Q: What if the app stops working?
            A: Contact your guardian or family member for help. They can reinstall the app if needed.
            
            📞 Emergency Contacts:
            • Guardian: Set in Guardian Settings
            • Police: 100
            • Cyber Crime: 1930
            
            🆘 Need More Help?
            Contact your guardian or a trusted family member.
        """.trimIndent()
        
        findViewById<TextView>(R.id.tvHelpContent).text = helpText
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

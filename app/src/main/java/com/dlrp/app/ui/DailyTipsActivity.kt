package com.dlrp.app.ui

import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R
import com.dlrp.app.voice.VoiceWarningTemplates

/**
 * Shows daily safety tips for elderly users.
 */
class DailyTipsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_tips)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Daily Safety Tips"
        
        loadTips()
    }
    
    private fun loadTips() {
        val tips = (0..9).map { index ->
            mapOf(
                "icon" to "💡",
                "tip" to VoiceWarningTemplates.getDailySafetyTip(index, "en")
            )
        }
        
        val listView = findViewById<ListView>(R.id.lvTips)
        val adapter = SimpleAdapter(
            this,
            tips,
            android.R.layout.simple_list_item_2,
            arrayOf("icon", "tip"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        listView.adapter = adapter
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

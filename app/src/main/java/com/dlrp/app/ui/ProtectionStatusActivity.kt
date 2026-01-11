package com.dlrp.app.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.dlrp.app.R
import com.dlrp.app.storage.EventLogStore

/**
 * Shows current protection status and statistics.
 */
class ProtectionStatusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protection_status)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Protection Status"
        
        loadProtectionStatus()
    }
    
    private fun loadProtectionStatus() {
        val eventLogStore = EventLogStore(this)
        val logs = eventLogStore.getLogs()
        
        // Calculate statistics
        val totalScamsBlocked = logs.filter { it.contains("DANGER") }.size
        val totalCallsProtected = logs.filter { it.contains("call") }.size
        val totalMessagesScanned = logs.size
        val criticalThreats = logs.filter { it.contains("CRITICAL") }.size
        
        // Update UI
        findViewById<TextView>(R.id.tvTotalScamsBlocked).text = totalScamsBlocked.toString()
        findViewById<TextView>(R.id.tvTotalCallsProtected).text = totalCallsProtected.toString()
        findViewById<TextView>(R.id.tvTotalMessagesScanned).text = totalMessagesScanned.toString()
        findViewById<TextView>(R.id.tvCriticalThreats).text = criticalThreats.toString()
        
        // Protection level
        val protectionLevel = when {
            criticalThreats > 5 -> "High Alert"
            totalScamsBlocked > 10 -> "Active Protection"
            else -> "Normal"
        }
        findViewById<TextView>(R.id.tvProtectionLevel).text = protectionLevel
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

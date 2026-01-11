package com.dlrp.app.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R
import com.dlrp.app.storage.EventLogStore

/**
 * Shows list of scanned messages with risk levels.
 */
class ScannedMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_messages)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Scanned Messages"
        
        loadMessages()
    }
    
    private fun loadMessages() {
        val eventLogStore = EventLogStore(this)
        val logs = eventLogStore.getLogs()
        
        val messagesList = if (logs.isEmpty()) {
            listOf("No messages scanned yet")
        } else {
            logs.takeLast(50).reversed() // Show last 50 messages
        }
        
        val listView = findViewById<ListView>(R.id.lvMessages)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messagesList)
        listView.adapter = adapter
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

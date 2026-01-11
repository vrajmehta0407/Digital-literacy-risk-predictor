package com.dlrp.app.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dlrp.app.R
import com.dlrp.app.storage.BlockedMessageStore

/**
 * Shows history of all blocked messages and calls.
 */
class BlockedHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlockedItemsAdapter
    private lateinit var blockedStore: BlockedMessageStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_history)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Blocked Scams History"
        
        blockedStore = BlockedMessageStore(this)
        
        recyclerView = findViewById(R.id.recyclerViewBlocked)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        loadBlockedItems()
    }
    
    private fun loadBlockedItems() {
        val items = blockedStore.getBlockedItems()
        adapter = BlockedItemsAdapter(items)
        recyclerView.adapter = adapter
        
        findViewById<TextView>(R.id.tvBlockedCount).text = 
            "Total Blocked: ${items.size} (${blockedStore.getBlockedMessages().size} messages, ${blockedStore.getBlockedCalls().size} calls)"
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    class BlockedItemsAdapter(private val items: List<BlockedMessageStore.BlockedItem>) : 
        RecyclerView.Adapter<BlockedItemsAdapter.ViewHolder>() {
        
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvType: TextView = view.findViewById(R.id.tvType)
            val tvSender: TextView = view.findViewById(R.id.tvSender)
            val tvContent: TextView = view.findViewById(R.id.tvContent)
            val tvReason: TextView = view.findViewById(R.id.tvReason)
            val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_blocked_message, parent, false)
            return ViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            
            holder.tvType.text = if (item.type == "SMS") "📱 SMS" else "📞 CALL"
            holder.tvSender.text = "From: ${item.sender}"
            holder.tvContent.text = if (item.content.length > 100) {
                item.content.take(100) + "..."
            } else {
                item.content
            }
            holder.tvReason.text = "⚠️ ${item.reason}"
            holder.tvTimestamp.text = item.timestamp
        }
        
        override fun getItemCount() = items.size
    }
}

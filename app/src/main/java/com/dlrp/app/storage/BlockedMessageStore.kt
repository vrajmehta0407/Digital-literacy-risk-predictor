package com.dlrp.app.storage

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Stores blocked messages and calls for history tracking.
 */
class BlockedMessageStore(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("blocked_messages", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    data class BlockedItem(
        val id: String,
        val type: String, // "SMS" or "CALL"
        val sender: String,
        val content: String,
        val reason: String,
        val timestamp: String,
        val riskLevel: String
    )
    
    /**
     * Add a blocked message to history.
     */
    fun addBlockedMessage(sender: String, body: String, reason: String, riskLevel: String) {
        val items = getBlockedItems().toMutableList()
        
        val newItem = BlockedItem(
            id = UUID.randomUUID().toString(),
            type = "SMS",
            sender = sender,
            content = body,
            reason = reason,
            timestamp = dateFormat.format(Date()),
            riskLevel = riskLevel
        )
        
        items.add(0, newItem) // Add to beginning
        
        // Keep only last 100 items
        if (items.size > 100) {
            items.subList(100, items.size).clear()
        }
        
        saveItems(items)
    }
    
    /**
     * Add a blocked call to history.
     */
    fun addBlockedCall(phoneNumber: String, reason: String, riskLevel: String) {
        val items = getBlockedItems().toMutableList()
        
        val newItem = BlockedItem(
            id = UUID.randomUUID().toString(),
            type = "CALL",
            sender = phoneNumber,
            content = "Incoming call",
            reason = reason,
            timestamp = dateFormat.format(Date()),
            riskLevel = riskLevel
        )
        
        items.add(0, newItem)
        
        if (items.size > 100) {
            items.subList(100, items.size).clear()
        }
        
        saveItems(items)
    }
    
    /**
     * Get all blocked items.
     */
    fun getBlockedItems(): List<BlockedItem> {
        val json = prefs.getString("items", "[]") ?: "[]"
        val items = mutableListOf<BlockedItem>()
        
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                items.add(BlockedItem(
                    id = obj.getString("id"),
                    type = obj.getString("type"),
                    sender = obj.getString("sender"),
                    content = obj.getString("content"),
                    reason = obj.getString("reason"),
                    timestamp = obj.getString("timestamp"),
                    riskLevel = obj.getString("riskLevel")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return items
    }
    
    /**
     * Get blocked messages only.
     */
    fun getBlockedMessages(): List<BlockedItem> {
        return getBlockedItems().filter { it.type == "SMS" }
    }
    
    /**
     * Get blocked calls only.
     */
    fun getBlockedCalls(): List<BlockedItem> {
        return getBlockedItems().filter { it.type == "CALL" }
    }
    
    /**
     * Get count of blocked items in last 7 days.
     */
    fun getWeeklyBlockedCount(): Int {
        val weekAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.time
        
        return getBlockedItems().count {
            try {
                val itemDate = dateFormat.parse(it.timestamp)
                itemDate?.after(weekAgo) == true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * Clear all blocked items.
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
    
    private fun saveItems(items: List<BlockedItem>) {
        val jsonArray = JSONArray()
        items.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("type", item.type)
                put("sender", item.sender)
                put("content", item.content)
                put("reason", item.reason)
                put("timestamp", item.timestamp)
                put("riskLevel", item.riskLevel)
            }
            jsonArray.put(obj)
        }
        
        prefs.edit().putString("items", jsonArray.toString()).apply()
    }
}

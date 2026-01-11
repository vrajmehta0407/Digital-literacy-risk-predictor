package com.dlrp.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dlrp.app.R
import com.dlrp.app.chatbot.ChatbotManager

class ChatbotActivity : AppCompatActivity() {

    private lateinit var chatbotManager: ChatbotManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatbotManager.ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Safety Assistant"

        val language = getSharedPreferences("app_settings", MODE_PRIVATE)
            .getString("language", "en") ?: "en"
        chatbotManager = ChatbotManager(language)

        initializeViews()
        setupChat()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewChat)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
    }

    private fun setupChat() {
        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Welcome message
        addBotMessage(chatbotManager.getResponse("hello"))

        // Send button
        btnSend.setOnClickListener {
            sendMessage()
        }

        // Quick replies
        setupQuickReplies()
    }

    private fun setupQuickReplies() {
        val quickReplies = chatbotManager.getQuickReplies()
        // TODO: Add quick reply buttons
    }

    private fun sendMessage() {
        val userInput = etMessage.text.toString().trim()
        if (userInput.isEmpty()) return

        // Add user message
        messages.add(ChatbotManager.ChatMessage(userInput, isBot = false))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)

        // Clear input
        etMessage.text.clear()

        // Get bot response
        val response = chatbotManager.getResponse(userInput)
        addBotMessage(response)
    }

    private fun addBotMessage(text: String) {
        messages.add(ChatbotManager.ChatMessage(text, isBot = true))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class ChatAdapter(private val messages: List<ChatbotManager.ChatMessage>) :
        RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutId = if (viewType == 0) R.layout.item_chat_bot else R.layout.item_chat_user
            val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tvMessage.text = messages[position].text
        }

        override fun getItemCount() = messages.size

        override fun getItemViewType(position: Int): Int {
            return if (messages[position].isBot) 0 else 1
        }
    }
}

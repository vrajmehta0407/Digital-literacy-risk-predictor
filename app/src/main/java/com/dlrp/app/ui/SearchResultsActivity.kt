package com.dlrp.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlrp.app.R

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var btnBack: ImageButton
    private lateinit var btnVoiceSearch: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter

    // Dummy data for search results (replace with real data sources)
    private val allItems = listOf(
        SearchResult("Risk Scanner checks SMS automatically", "Feature"),
        SearchResult("Never share OTP with anyone", "Tip"),
        SearchResult("Bank officials never ask for passwords", "Tip"),
        SearchResult("How to block spam calls", "Help"),
        SearchResult("Setting up a Guardian", "Help"),
        SearchResult("Premium Protection Features", "Feature"),
        SearchResult("Report a new scam number", "Action"),
        SearchResult("Update virus definitions", "Action"),
        SearchResult("Enable voice alerts", "Setting"),
        SearchResult("Change app language", "Setting"),
        SearchResult("View blocked messages", "History"),
        SearchResult("Emergency call shortcut", "Feature")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        initializeViews()
        setupSearch()
    }

    private fun initializeViews() {
        etSearch = findViewById(R.id.etSearch)
        btnBack = findViewById(R.id.btnBack)
        btnVoiceSearch = findViewById(R.id.btnVoiceSearch)
        recyclerView = findViewById(R.id.recyclerViewResults)
    }

    private fun setupSearch() {
        adapter = SearchAdapter(allItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }

        btnVoiceSearch.setOnClickListener {
            Toast.makeText(this, "Voice search coming soon!", Toast.LENGTH_SHORT).show()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterResults(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterResults(etSearch.text.toString())
                true
            } else {
                false
            }
        }
        
        // Focus and show keyboard automatically
        etSearch.requestFocus()
    }

    private fun filterResults(query: String) {
        val filteredList = if (query.isEmpty()) {
            allItems
        } else {
            allItems.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.type.contains(query, ignoreCase = true) 
            }
        }
        adapter.updateList(filteredList)
    }

    data class SearchResult(val title: String, val type: String)

    class SearchAdapter(private var items: List<SearchResult>) :
        RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTitle: TextView = view.findViewById(android.R.id.text1)
            val tvType: TextView = view.findViewById(android.R.id.text2)
        }

        fun updateList(newItems: List<SearchResult>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvTitle.text = item.title
            holder.tvType.text = item.type
        }

        override fun getItemCount() = items.size
    }
}

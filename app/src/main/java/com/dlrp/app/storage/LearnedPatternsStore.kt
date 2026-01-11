package com.dlrp.app.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Stores learned scam patterns and keywords locally.
 * Enables offline learning and pattern sharing.
 */
class LearnedPatternsStore(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "learned_patterns", Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_KEYWORDS = "learned_keywords"
        private const val KEY_PATTERNS = "learned_patterns"
        private const val KEY_VERSION = "patterns_version"
        private const val KEY_LAST_UPDATE = "last_update"
    }
    
    /**
     * Add a learned keyword.
     */
    fun addLearnedKeyword(keyword: String, source: String) {
        val keywords = getAllLearnedKeywords().toMutableSet()
        keywords.add(keyword)
        saveKeywords(keywords)
        updateTimestamp()
    }
    
    /**
     * Add a learned pattern (phrase).
     */
    fun addLearnedPattern(pattern: String, source: String) {
        val patterns = getAllLearnedPatterns().toMutableSet()
        patterns.add(pattern)
        savePatterns(patterns)
        updateTimestamp()
    }
    
    /**
     * Get all learned keywords.
     */
    fun getAllLearnedKeywords(): List<String> {
        val keywordsString = prefs.getString(KEY_KEYWORDS, "") ?: ""
        return if (keywordsString.isEmpty()) {
            emptyList()
        } else {
            keywordsString.split("|").filter { it.isNotEmpty() }
        }
    }
    
    /**
     * Get all learned patterns.
     */
    fun getAllLearnedPatterns(): List<String> {
        val patternsString = prefs.getString(KEY_PATTERNS, "") ?: ""
        return if (patternsString.isEmpty()) {
            emptyList()
        } else {
            patternsString.split("|").filter { it.isNotEmpty() }
        }
    }
    
    private fun saveKeywords(keywords: Set<String>) {
        prefs.edit().putString(KEY_KEYWORDS, keywords.joinToString("|")).apply()
    }
    
    private fun savePatterns(patterns: Set<String>) {
        prefs.edit().putString(KEY_PATTERNS, patterns.joinToString("|")).apply()
    }
    
    private fun updateTimestamp() {
        prefs.edit().putLong(KEY_LAST_UPDATE, System.currentTimeMillis()).apply()
        incrementVersion()
    }
    
    private fun incrementVersion() {
        val currentVersion = prefs.getInt(KEY_VERSION, 0)
        prefs.edit().putInt(KEY_VERSION, currentVersion + 1).apply()
    }
    
    /**
     * Log a learning event.
     */
    fun logLearningEvent(message: String) {
        // Could be extended to maintain a learning history
        android.util.Log.d("LearnedPatternsStore", message)
    }
    
    /**
     * Export learned patterns for sharing.
     */
    fun exportPatterns(): String {
        val keywords = getAllLearnedKeywords()
        val patterns = getAllLearnedPatterns()
        return "KEYWORDS:${keywords.joinToString(",")}|PATTERNS:${patterns.joinToString(",")}"
    }
    
    /**
     * Import learned patterns from another device.
     */
    fun importPatterns(data: String) {
        try {
            val parts = data.split("|")
            if (parts.size == 2) {
                val keywordsPart = parts[0].removePrefix("KEYWORDS:")
                val patternsPart = parts[1].removePrefix("PATTERNS:")
                
                val keywords = keywordsPart.split(",").filter { it.isNotEmpty() }
                val patterns = patternsPart.split(",").filter { it.isNotEmpty() }
                
                keywords.forEach { addLearnedKeyword(it, "import") }
                patterns.forEach { addLearnedPattern(it, "import") }
            }
        } catch (e: Exception) {
            android.util.Log.e("LearnedPatternsStore", "Import failed", e)
        }
    }
    
    /**
     * Clear all learned patterns.
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}

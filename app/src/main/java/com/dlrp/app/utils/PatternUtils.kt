package com.dlrp.app.utils

import java.util.regex.Pattern

object PatternUtils {
    
    fun contains(text: String, patterns: List<String>): Boolean {
        val lower = text.lowercase()
        return patterns.any { lower.contains(it) }
    }
}

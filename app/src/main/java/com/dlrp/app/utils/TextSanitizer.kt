package com.dlrp.app.utils

object TextSanitizer {
    
    fun clean(text: String): String {
        // Remove weird invisible chars sometimes used in scams to bypass filters
        return text.replace(Regex("[^\\x00-\\x7F]"), "")
    }
}

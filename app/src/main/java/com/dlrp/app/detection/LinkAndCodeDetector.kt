package com.dlrp.app.detection

import android.util.Patterns
import java.util.regex.Pattern

class LinkAndCodeDetector {

    // Regex for 4-8 digit codes, allowing for some spacing or grouping
    private val CODE_PATTERN = Pattern.compile("(?<!\\d)\\d{4,8}(?!\\d)")

    fun containsLink(text: String): Boolean {
        return Patterns.WEB_URL.matcher(text).find()
    }

    fun containsCode(text: String): Boolean {
        return CODE_PATTERN.matcher(text).find()
    }

    fun extractCode(text: String): String? {
        val matcher = CODE_PATTERN.matcher(text)
        if (matcher.find()) {
            return matcher.group()
        }
        return null
    }
}

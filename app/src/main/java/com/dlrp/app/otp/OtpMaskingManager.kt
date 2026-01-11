package com.dlrp.app.otp

import java.util.regex.Pattern

object OtpMaskingManager {

    private val CODE_PATTERN = Pattern.compile("(?<!\\d)\\d{4,8}(?!\\d)")

    fun maskContent(text: String): String {
        val matcher = CODE_PATTERN.matcher(text)
        return matcher.replaceAll("******")
    }
}

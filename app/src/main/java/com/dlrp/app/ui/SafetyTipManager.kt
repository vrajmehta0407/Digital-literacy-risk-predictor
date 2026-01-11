package com.dlrp.app.ui

import java.util.Random

class SafetyTipManager {

    private val tips = listOf(
        "Banks never ask for your PIN or OTP over the phone.",
        "If a caller rushes you, hang up immediately.",
        "Do not click links in messages from unknown numbers.",
        "Your family will never ask for money via a strange link.",
        "Government officials will never call you on WhatsApp.",
        "If you are unsure, press the Help button.",
        "Keep your phone locked when not in use."
    )

    fun getRandomTip(): String {
        return tips[Random().nextInt(tips.size)]
    }
}

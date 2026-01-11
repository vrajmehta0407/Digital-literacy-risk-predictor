package com.dlrp.app.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.dlrp.app.risk.RiskLevel

class SimpleStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    // Placeholder view logic
    fun setStatus(level: RiskLevel) {
        // update background color and text
    }
}

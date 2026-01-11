package com.dlrp.app.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.dlrp.app.R

/**
 * Privacy indicator UI component showing offline protection status.
 * Builds user trust by clearly displaying privacy features.
 */
class PrivacyIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    private val statusText: TextView
    private val detailsText: TextView
    
    init {
        orientation = VERTICAL
        setPadding(16, 16, 16, 16)
        
        // Create status text
        statusText = TextView(context).apply {
            text = "🔒 100% Offline Protection"
            textSize = 16f
            setTextColor(context.getColor(android.R.color.holo_green_dark))
        }
        addView(statusText)
        
        // Create details text
        detailsText = TextView(context).apply {
            text = "No messages are sent to any server. All protection happens on your device."
            textSize = 12f
            setTextColor(context.getColor(android.R.color.darker_gray))
            setPadding(0, 8, 0, 0)
        }
        addView(detailsText)
        
        // Make clickable for more info
        isClickable = true
        setOnClickListener {
            showPrivacyDetails()
        }
    }
    
    private fun showPrivacyDetails() {
        // Show dialog with privacy policy details
        android.app.AlertDialog.Builder(context)
            .setTitle("Privacy & Security")
            .setMessage(
                "Safe Senior protects your privacy:\n\n" +
                "✓ All scam detection happens on your device\n" +
                "✓ No messages are sent to external servers\n" +
                "✓ Your data never leaves your phone\n" +
                "✓ Guardian notifications use SMS (no cloud)\n" +
                "✓ Learned patterns stored locally\n\n" +
                "Your safety and privacy are our top priorities."
            )
            .setPositiveButton("OK", null)
            .show()
    }
}

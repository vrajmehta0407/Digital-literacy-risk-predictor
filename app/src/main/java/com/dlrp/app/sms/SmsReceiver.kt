package com.dlrp.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.dlrp.app.core.AppInitializer
import com.dlrp.app.risk.RiskLevel
import com.dlrp.app.storage.BlockedMessageStore

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SmsReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            
            // Use pendingResult to handle async processing
            val pendingResult = goAsync()
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    messages.forEach { sms ->
                        sms?.let {
                            val sender = it.originatingAddress ?: "Unknown"
                            val body = it.messageBody ?: ""
                            
                            Log.d(TAG, "Received SMS from: $sender")
                            
                            // Analyze message
                            val analyzer = SmsAnalyzer(context)
                            val shouldBlock = analyzer.processMessage(sender, body)
                            
                            if (shouldBlock) {
                                Log.d(TAG, "BLOCKING scam message from: $sender")
                                
                                // Abort broadcast to prevent message from reaching inbox
                                withContext(Dispatchers.Main) {
                                    abortBroadcast()
                                }
                                
                                // Store in blocked messages history
                                val blockedStore = BlockedMessageStore(context)
                                blockedStore.addBlockedMessage(
                                    sender = sender,
                                    body = body,
                                    reason = "Scam detected - OTP/suspicious content",
                                    riskLevel = "DANGER"
                                )
                                
                                Log.d(TAG, "Message blocked and stored in history")
                            } else {
                                Log.d(TAG, "Message from $sender is safe, allowing through")
                            }
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

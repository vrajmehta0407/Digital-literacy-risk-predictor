package com.dlrp.app.guardian

import android.content.Context
import com.dlrp.app.storage.TrustedContactsStore

/**
 * Manages contact approval for high-risk users.
 * Requires guardian approval for new contact interactions.
 */
class ContactApprovalManager(
    private val context: Context,
    private val guardianNotifier: GuardianNotifier
) {
    
    private val trustedContactsStore = TrustedContactsStore(context)
    private val pendingApprovals = mutableMapOf<String, PendingContact>()
    
    /**
     * Check if contact approval mode is enabled.
     */
    fun isApprovalModeEnabled(): Boolean {
        val prefs = context.getSharedPreferences("contact_approval", Context.MODE_PRIVATE)
        return prefs.getBoolean("approval_mode_enabled", false)
    }
    
    /**
     * Enable or disable contact approval mode.
     */
    fun setApprovalMode(enabled: Boolean) {
        val prefs = context.getSharedPreferences("contact_approval", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("approval_mode_enabled", enabled).apply()
    }
    
    /**
     * Check if contact needs approval.
     * @return True if contact is new and approval mode is enabled
     */
    fun needsApproval(phoneNumber: String): Boolean {
        if (!isApprovalModeEnabled()) return false
        
        // Check if contact is already trusted
        if (trustedContactsStore.isTrusted(phoneNumber)) {
            return false
        }
        
        // Check if already pending
        if (pendingApprovals.containsKey(phoneNumber)) {
            return true
        }
        
        // New contact - needs approval
        return true
    }
    
    /**
     * Request guardian approval for new contact.
     */
    fun requestApproval(phoneNumber: String, messagePreview: String) {
        val pending = PendingContact(
            phoneNumber = phoneNumber,
            messagePreview = messagePreview,
            requestTime = System.currentTimeMillis()
        )
        
        pendingApprovals[phoneNumber] = pending
        
        // Notify guardian
        guardianNotifier.sendContactApprovalRequest(phoneNumber, messagePreview)
    }
    
    /**
     * Approve a contact (called by guardian).
     */
    fun approveContact(phoneNumber: String) {
        trustedContactsStore.addTrustedContact(phoneNumber)
        pendingApprovals.remove(phoneNumber)
    }
    
    /**
     * Reject a contact (called by guardian).
     */
    fun rejectContact(phoneNumber: String) {
        pendingApprovals.remove(phoneNumber)
        // Could add to blocked list
    }
    
    /**
     * Get all pending approvals.
     */
    fun getPendingApprovals(): List<PendingContact> {
        return pendingApprovals.values.toList()
    }
    
    data class PendingContact(
        val phoneNumber: String,
        val messagePreview: String,
        val requestTime: Long
    )
}

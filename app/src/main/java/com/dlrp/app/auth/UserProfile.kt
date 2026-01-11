package com.dlrp.app.auth

import java.util.*

/**
 * User profile data model.
 */
data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val photoPath: String? = null,
    val isPremium: Boolean = false,
    val premiumExpiryDate: Long? = null,
    val trialStartDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val securityQuestion: String? = null,
    val securityAnswer: String? = null
) {
    fun isTrialActive(): Boolean {
        if (trialStartDate == null) return false
        val trialEndDate = trialStartDate + (7 * 24 * 60 * 60 * 1000) // 7 days
        return System.currentTimeMillis() < trialEndDate
    }
    
    fun isPremiumActive(): Boolean {
        if (isPremium && premiumExpiryDate != null) {
            return System.currentTimeMillis() < premiumExpiryDate
        }
        return isTrialActive()
    }
    
    fun getDaysUntilExpiry(): Int {
        val expiryDate = premiumExpiryDate ?: return 0
        val daysLeft = (expiryDate - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)
        return daysLeft.toInt()
    }
}

/**
 * Subscription data model.
 */
data class Subscription(
    val userId: String,
    val plan: String, // "free", "trial", "premium"
    val price: Int = 500,
    val startDate: Long = System.currentTimeMillis(),
    val expiryDate: Long,
    val paymentId: String? = null,
    val isActive: Boolean = true,
    val autoRenew: Boolean = false
)

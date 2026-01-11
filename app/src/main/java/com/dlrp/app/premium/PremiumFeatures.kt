package com.dlrp.app.premium

/**
 * Premium feature flags and management.
 */
object PremiumFeatures {
    
    enum class Feature {
        // Free features (always available)
        BASIC_SCAM_DETECTION,
        VOICE_ALERTS,
        SINGLE_GUARDIAN,
        DAILY_TIPS,
        BASIC_HISTORY,
        
        // Premium features (₹500/year)
        ADVANCED_AI_DETECTION,
        AUTO_CALL_BLOCKING,
        MULTIPLE_GUARDIANS,
        REAL_TIME_UPDATES,
        CLOUD_BACKUP,
        CALL_RECORDING,
        LOCATION_SHARING,
        DETAILED_ANALYTICS,
        PRIORITY_SUPPORT,
        AD_FREE
    }
    
    /**
     * Check if a feature is available for the user.
     */
    fun isFeatureAvailable(feature: Feature, isPremium: Boolean): Boolean {
        return when (feature) {
            // Free features
            Feature.BASIC_SCAM_DETECTION,
            Feature.VOICE_ALERTS,
            Feature.SINGLE_GUARDIAN,
            Feature.DAILY_TIPS,
            Feature.BASIC_HISTORY -> true
            
            // Premium features
            Feature.ADVANCED_AI_DETECTION,
            Feature.AUTO_CALL_BLOCKING,
            Feature.MULTIPLE_GUARDIANS,
            Feature.REAL_TIME_UPDATES,
            Feature.CLOUD_BACKUP,
            Feature.CALL_RECORDING,
            Feature.LOCATION_SHARING,
            Feature.DETAILED_ANALYTICS,
            Feature.PRIORITY_SUPPORT,
            Feature.AD_FREE -> isPremium
        }
    }
    
    /**
     * Get feature description.
     */
    fun getFeatureDescription(feature: Feature): String {
        return when (feature) {
            Feature.BASIC_SCAM_DETECTION -> "Basic scam pattern detection"
            Feature.VOICE_ALERTS -> "Voice warnings for scams"
            Feature.SINGLE_GUARDIAN -> "Add 1 guardian contact"
            Feature.DAILY_TIPS -> "Daily safety tips"
            Feature.BASIC_HISTORY -> "Last 30 days history"
            
            Feature.ADVANCED_AI_DETECTION -> "AI-powered advanced scam detection with 99% accuracy"
            Feature.AUTO_CALL_BLOCKING -> "Automatically block spam calls"
            Feature.MULTIPLE_GUARDIANS -> "Add up to 5 guardian contacts"
            Feature.REAL_TIME_UPDATES -> "Real-time threat database updates"
            Feature.CLOUD_BACKUP -> "Cloud backup and sync across devices"
            Feature.CALL_RECORDING -> "Record suspicious calls for evidence"
            Feature.LOCATION_SHARING -> "Share location with guardians in emergency"
            Feature.DETAILED_ANALYTICS -> "Detailed threat analytics and reports"
            Feature.PRIORITY_SUPPORT -> "24/7 priority customer support"
            Feature.AD_FREE -> "Ad-free experience"
        }
    }
    
    /**
     * Get all premium features.
     */
    fun getPremiumFeatures(): List<Feature> {
        return listOf(
            Feature.ADVANCED_AI_DETECTION,
            Feature.AUTO_CALL_BLOCKING,
            Feature.MULTIPLE_GUARDIANS,
            Feature.REAL_TIME_UPDATES,
            Feature.CLOUD_BACKUP,
            Feature.CALL_RECORDING,
            Feature.LOCATION_SHARING,
            Feature.DETAILED_ANALYTICS,
            Feature.PRIORITY_SUPPORT,
            Feature.AD_FREE
        )
    }
}

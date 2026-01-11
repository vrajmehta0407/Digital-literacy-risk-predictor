package com.dlrp.app.utils

object TimeWindowUtils {
    
    fun isWithinRange(timestamp: Long, durationMillis: Long): Boolean {
        return (System.currentTimeMillis() - timestamp) <= durationMillis
    }
}

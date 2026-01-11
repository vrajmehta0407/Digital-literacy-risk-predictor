package com.dlrp.app.storage

import android.content.Context
import android.provider.ContactsContract

class TrustedContactsStore(private val context: Context) {

    fun isTrusted(phoneNumber: String): Boolean {
        // Query Android Contacts Provider
        val uri = android.net.Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, 
            android.net.Uri.encode(phoneNumber)
        )
        
        val cursor = context.contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        
        return cursor?.use {
            if (it.moveToFirst()) {
                true // Number exists in contacts
            } else {
                false
            }
        } ?: false
    }
    
    /**
     * Add a contact to the trusted list.
     */
    fun addTrustedContact(phoneNumber: String) {
        // In a real implementation, this would add the contact to a local database
        // For now, we'll just log it
        android.util.Log.d("TrustedContactsStore", "Added trusted contact: $phoneNumber")
    }
}

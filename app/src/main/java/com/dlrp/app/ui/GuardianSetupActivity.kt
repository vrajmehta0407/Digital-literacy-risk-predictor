package com.dlrp.app.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R
import com.dlrp.app.core.AppInitializer

class GuardianSetupActivity : AppCompatActivity() {

    private lateinit var tvCurrentGuardian: TextView
    private val PICK_CONTACT_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian_setup)

        tvCurrentGuardian = findViewById(R.id.tvCurrentGuardian)
        val btnSelectContact = findViewById<Button>(R.id.btnSelectContact)

        updateCurrentGuardianDisplay()

        btnSelectContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, PICK_CONTACT_REQUEST)
        }
    }

    private fun updateCurrentGuardianDisplay() {
        val guardian = AppInitializer.instance.guardianManager.getGuardianContact()
        if (guardian != null) {
            tvCurrentGuardian.text = "Current Guardian:\n${guardian.name}\n${guardian.phoneNumber}"
        } else {
            tvCurrentGuardian.text = "No Guardian Selected"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                saveContactFromUri(uri)
            }
        }
    }

    private fun saveContactFromUri(uri: Uri) {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)

                AppInitializer.instance.guardianManager.setGuardianContact(name, number)
                updateCurrentGuardianDisplay()
                Toast.makeText(this, "Guardian Updated!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}

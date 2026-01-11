package com.dlrp.app.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R

/**
 * Language selection activity for multilingual support.
 */
class LanguageSettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_settings)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Language Settings"
        
        prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        
        setupLanguageSelection()
    }
    
    private fun setupLanguageSelection() {
        val radioGroup = findViewById<RadioGroup>(R.id.rgLanguages)
        val currentLanguage = prefs.getString("language", "en") ?: "en"
        
        // Set current selection
        when (currentLanguage) {
            "en" -> findViewById<RadioButton>(R.id.rbEnglish).isChecked = true
            "hi" -> findViewById<RadioButton>(R.id.rbHindi).isChecked = true
            "gu" -> findViewById<RadioButton>(R.id.rbGujarati).isChecked = true
            "ta" -> findViewById<RadioButton>(R.id.rbTamil).isChecked = true
            "te" -> findViewById<RadioButton>(R.id.rbTelugu).isChecked = true
            "bn" -> findViewById<RadioButton>(R.id.rbBengali).isChecked = true
        }
        
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val language = when (checkedId) {
                R.id.rbEnglish -> "en"
                R.id.rbHindi -> "hi"
                R.id.rbGujarati -> "gu"
                R.id.rbTamil -> "ta"
                R.id.rbTelugu -> "te"
                R.id.rbBengali -> "bn"
                else -> "en"
            }
            
            prefs.edit().putString("language", language).apply()
            Toast.makeText(this, "Language changed to ${getLanguageName(language)}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun getLanguageName(code: String): String {
        return when (code) {
            "en" -> "English"
            "hi" -> "Hindi"
            "gu" -> "Gujarati"
            "ta" -> "Tamil"
            "te" -> "Telugu"
            "bn" -> "Bengali"
            else -> "English"
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

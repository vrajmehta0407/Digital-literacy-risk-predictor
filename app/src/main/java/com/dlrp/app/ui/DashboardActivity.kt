package com.dlrp.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dlrp.app.R
import com.dlrp.app.core.AppInitializer
import com.dlrp.app.guardian.GuardianPreferences
import com.dlrp.app.storage.EventLogStore
import com.dlrp.app.voice.DailySafetyReminder
import com.google.android.material.navigation.NavigationView

/**
 * Beautiful, elderly-friendly dashboard with navigation drawer.
 * Features large icons, clear text, and easy navigation.
 */
class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    
    // Stats TextViews
    private lateinit var tvScamsBlocked: TextView
    private lateinit var tvCallsProtected: TextView
    private lateinit var tvDailyTip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        
        initializeViews()
        setupToolbar()
        setupNavigationDrawer()
        loadProtectionStats()
        loadDailyTip()
        
        // Request all necessary permissions
        requestAllPermissions()
    }
    
    private fun requestAllPermissions() {
        val permissionsNeeded = mutableListOf<String>()
        
        // Check SMS permissions
        if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.RECEIVE_SMS)
        }
        if (checkSelfPermission(android.Manifest.permission.READ_SMS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.READ_SMS)
        }
        if (checkSelfPermission(android.Manifest.permission.SEND_SMS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.SEND_SMS)
        }
        
        // Check Phone permissions
        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE)
        }
        if (checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.READ_CALL_LOG)
        }
        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.CALL_PHONE)
        }
        
        // Check Contacts permission
        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.READ_CONTACTS)
        }
        
        if (permissionsNeeded.isNotEmpty()) {
            // Show explanation dialog first
            android.app.AlertDialog.Builder(this)
                .setTitle("🛡️ Protection Permissions Needed")
                .setMessage(
                    "Safe Senior needs these permissions to protect you:\n\n" +
                    "📱 SMS: To scan messages for scams\n" +
                    "📞 Phone: To detect suspicious calls\n" +
                    "👥 Contacts: To identify trusted senders\n\n" +
                    "Your data stays on your phone. Nothing is sent online."
                )
                .setPositiveButton("Grant Permissions") { _, _ ->
                    requestPermissions(permissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
                }
                .setNegativeButton("Not Now") { _, _ ->
                    Toast.makeText(this, "App needs permissions to protect you from scams", Toast.LENGTH_LONG).show()
                }
                .setCancelable(false)
                .show()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == android.content.pm.PackageManager.PERMISSION_GRANTED }
            
            if (allGranted) {
                Toast.makeText(this, "✅ Protection Active! You are now protected from scams.", Toast.LENGTH_LONG).show()
            } else {
                android.app.AlertDialog.Builder(this)
                    .setTitle("⚠️ Limited Protection")
                    .setMessage(
                        "Some permissions were denied. The app may not be able to protect you fully.\n\n" +
                        "Please grant all permissions in Settings for complete protection."
                    )
                    .setPositiveButton("Open Settings") { _, _ ->
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = android.net.Uri.parse("package:$packageName")
                        startActivity(intent)
                    }
                    .setNegativeButton("OK", null)
                    .show()
            }
        }
    }
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
    
    private fun initializeViews() {
        // Search Bar
        findViewById<android.view.View>(R.id.cardSearch).setOnClickListener {
            startActivity(Intent(this, SearchResultsActivity::class.java))
        }

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        
        tvScamsBlocked = findViewById(R.id.tvScamsBlocked)
        tvCallsProtected = findViewById(R.id.tvCallsProtected)
        tvDailyTip = findViewById(R.id.tvDailyTip)
        
        // Quick action buttons (new UI)
        findViewById<android.view.View>(R.id.btnQuickEmergency).setOnClickListener {
            callGuardian()
        }
        findViewById<android.view.View>(R.id.btnQuickGuardian).setOnClickListener {
            startActivity(Intent(this, GuardianSetupActivity::class.java))
        }
        findViewById<android.view.View>(R.id.btnQuickTips).setOnClickListener {
            startActivity(Intent(this, DailyTipsActivity::class.java))
        }
        findViewById<android.view.View>(R.id.btnQuickSettings).setOnClickListener {
            startActivity(Intent(this, VoiceSettingsActivity::class.java))
        }
        
        // Activity cards
        findViewById<android.view.View>(R.id.cardScamsBlocked).setOnClickListener {
            startActivity(Intent(this, ProtectionStatusActivity::class.java))
        }
        findViewById<android.view.View>(R.id.cardCallsProtected).setOnClickListener {
            startActivity(Intent(this, ProtectionStatusActivity::class.java))
        }
        findViewById<android.view.View>(R.id.cardDailyTip).setOnClickListener {
            startActivity(Intent(this, DailyTipsActivity::class.java))
        }
        findViewById<android.view.View>(R.id.cardProtectionStatus).setOnClickListener {
            startActivity(Intent(this, ProtectionStatusActivity::class.java))
        }
        findViewById<TextView>(R.id.tvSeeAllActivity).setOnClickListener {
            startActivity(Intent(this, ScannedMessagesActivity::class.java))
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        
        findViewById<android.widget.ImageView>(R.id.btnMenu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    
    private fun setupNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        navigationView.setNavigationItemSelectedListener(this)
    }
    
    private fun loadProtectionStats() {
        // Get stats from BlockedMessageStore (actual blocked items)
        val blockedStore = com.dlrp.app.storage.BlockedMessageStore(this)
        
        val blockedMessages = blockedStore.getBlockedMessages().size
        val blockedCalls = blockedStore.getBlockedCalls().size
        
        tvScamsBlocked.text = "$blockedMessages Scams Blocked"
        tvCallsProtected.text = "$blockedCalls Calls Protected"
    }
    
    private fun loadDailyTip() {
        // Get a random tip number (0-9)
        val tipNumber = (0..9).random()
        val tip = com.dlrp.app.voice.VoiceWarningTemplates.getDailySafetyTip(tipNumber, "en")
        tvDailyTip.text = tip
    }
    
    private fun callGuardian() {
        val guardianPrefs = GuardianPreferences(this)
        val guardianPhone = guardianPrefs.getGuardianPhone()
        
        if (guardianPhone.isNotEmpty()) {
            // Log emergency call
            EventLogStore.logEvent(
                type = "EMERGENCY_CALL_INITIATED",
                details = "User initiated emergency call from dashboard",
                riskLevel = "CRITICAL"
            )
            
            // Notify guardian
            val guardianNotifier = com.dlrp.app.guardian.GuardianNotifier(this)
            guardianNotifier.sendEmergencyCallNotification(
                "User pressed emergency call button on dashboard"
            )
            
            // Initiate phone call
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$guardianPhone")
            }
            startActivity(callIntent)
        } else {
            Toast.makeText(
                this,
                "Please set up a guardian contact in settings first",
                Toast.LENGTH_LONG
            ).show()
            
            // Open guardian setup
            startActivity(Intent(this, GuardianSetupActivity::class.java))
        }
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                // Already on dashboard
            }
            R.id.nav_premium -> {
                startActivity(Intent(this, PremiumActivity::class.java))
            }
            R.id.nav_chatbot -> {
                startActivity(Intent(this, ChatbotActivity::class.java))
            }
            R.id.nav_protection -> {
                startActivity(Intent(this, ProtectionStatusActivity::class.java))
            }
            R.id.nav_messages -> {
                startActivity(Intent(this, BlockedHistoryActivity::class.java))
            }
            R.id.nav_calls -> {
                startActivity(Intent(this, ScannedMessagesActivity::class.java)) // Reuse messages for now
            }
            R.id.nav_daily_tips -> {
                startActivity(Intent(this, DailyTipsActivity::class.java))
            }
            R.id.nav_guardian -> {
                startActivity(Intent(this, GuardianSetupActivity::class.java))
            }
            R.id.nav_emergency -> {
                callGuardian()
            }
            R.id.nav_language -> {
                startActivity(Intent(this, LanguageSettingsActivity::class.java))
            }
            R.id.nav_voice -> {
                startActivity(Intent(this, VoiceSettingsActivity::class.java))
            }
            R.id.nav_privacy -> {
                showPrivacyInfo()
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, VoiceSettingsActivity::class.java)) // Reuse voice settings for now
            }
            R.id.nav_tutorial -> {
                startActivity(Intent(this, TutorialActivity::class.java))
            }
            R.id.nav_help -> {
                startActivity(Intent(this, HelpActivity::class.java))
            }
            R.id.nav_about -> {
                showAboutDialog()
            }
        }
        
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    
    private fun showPrivacyInfo() {
        android.app.AlertDialog.Builder(this)
            .setTitle("🔒 Privacy & Security")
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
    
    private fun showAboutDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("ℹ️ About Safe Senior")
            .setMessage(
                "Safe Senior - Digital Literacy Risk Predictor\n\n" +
                "Version 1.0\n\n" +
                "Protecting elderly users from scams and fraud with:\n" +
                "• AI-powered scam detection\n" +
                "• Multilingual support\n" +
                "• Guardian notifications\n" +
                "• 100% offline protection\n\n" +
                "Stay safe, stay protected! 🛡️"
            )
            .setPositiveButton("OK", null)
            .show()
    }
    
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

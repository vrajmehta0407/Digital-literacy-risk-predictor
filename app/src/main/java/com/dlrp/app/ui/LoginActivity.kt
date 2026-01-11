package com.dlrp.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R
import com.dlrp.app.auth.UserManager
import com.google.android.material.textfield.TextInputEditText

/**
 * Login screen for user authentication.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var userManager: UserManager
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnBiometric: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userManager = UserManager(this)

        // Check if already logged in
        if (userManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnBiometric = findViewById(R.id.btnBiometric)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvSignup = findViewById(R.id.tvSignup)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            performLogin()
        }

        btnBiometric.setOnClickListener {
            Toast.makeText(this, "Biometric login coming soon!", Toast.LENGTH_SHORT).show()
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        // Validation
        if (email.isEmpty()) {
            etEmail.error = "Please enter email or phone"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Please enter password"
            etPassword.requestFocus()
            return
        }

        // Show loading
        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."

        // Attempt login
        val result = userManager.login(email, password)
        
        result.onSuccess { user ->
            Toast.makeText(this, "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
            navigateToDashboard()
        }.onFailure { error ->
            btnLogin.isEnabled = true
            btnLogin.text = "Login"
            Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        // Prevent going back, must login
        finishAffinity()
    }
}

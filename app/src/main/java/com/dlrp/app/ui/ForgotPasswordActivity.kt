package com.dlrp.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dlrp.app.R
import com.dlrp.app.auth.UserManager
import com.google.android.material.textfield.TextInputEditText

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var userManager: UserManager
    private lateinit var etEmail: TextInputEditText
    private lateinit var etSecurityAnswer: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var btnResetPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        userManager = UserManager(this)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reset Password"

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer)
        etNewPassword = findViewById(R.id.etNewPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)
    }

    private fun setupListeners() {
        btnResetPassword.setOnClickListener {
            performPasswordReset()
        }
    }

    private fun performPasswordReset() {
        val email = etEmail.text.toString().trim()
        val securityAnswer = etSecurityAnswer.text.toString().trim()
        val newPassword = etNewPassword.text.toString()

        if (email.isEmpty()) {
            etEmail.error = "Please enter email"
            return
        }

        if (securityAnswer.isEmpty()) {
            etSecurityAnswer.error = "Please enter security answer"
            return
        }

        if (newPassword.isEmpty() || newPassword.length < 6) {
            etNewPassword.error = "Password must be at least 6 characters"
            return
        }

        btnResetPassword.isEnabled = false
        btnResetPassword.text = "Resetting..."

        val result = userManager.resetPassword(email, securityAnswer, newPassword)
        
        result.onSuccess {
            Toast.makeText(this, "Password reset successful! Please login.", Toast.LENGTH_LONG).show()
            finish()
        }.onFailure { error ->
            btnResetPassword.isEnabled = true
            btnResetPassword.text = "Reset Password"
            Toast.makeText(this, "Reset failed: ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

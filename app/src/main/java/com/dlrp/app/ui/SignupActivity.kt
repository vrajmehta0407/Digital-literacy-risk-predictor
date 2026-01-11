package com.dlrp.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dlrp.app.R
import com.dlrp.app.auth.UserManager
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream

class SignupActivity : AppCompatActivity() {

    private lateinit var userManager: UserManager
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnChoosePhoto: Button
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignup: Button
    private lateinit var tvLogin: TextView

    private var photoPath: String? = null
    private val CAMERA_REQUEST = 100
    private val GALLERY_REQUEST = 101
    private val CAMERA_PERMISSION_REQUEST = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        userManager = UserManager(this)
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignup = findViewById(R.id.btnSignup)
        tvLogin = findViewById(R.id.tvLogin)
    }

    private fun setupListeners() {
        btnTakePhoto.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        btnChoosePhoto.setOnClickListener {
            openGallery()
        }

        btnSignup.setOnClickListener {
            performSignup()
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    val photo = data?.extras?.get("data") as? Bitmap
                    photo?.let {
                        ivProfilePhoto.setImageBitmap(it)
                        photoPath = savePhotoToFile(it)
                    }
                }
                GALLERY_REQUEST -> {
                    val uri = data?.data
                    uri?.let {
                        ivProfilePhoto.setImageURI(it)
                        photoPath = it.toString()
                    }
                }
            }
        }
    }

    private fun savePhotoToFile(bitmap: Bitmap): String {
        val file = File(filesDir, "profile_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file.absolutePath
    }

    private fun performSignup() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        // Validation
        if (name.isEmpty()) {
            etName.error = "Please enter your name"
            etName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Please enter email"
            etEmail.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            etPhone.error = "Please enter phone number"
            etPhone.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Please enter password"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return
        }

        // Show loading
        btnSignup.isEnabled = false
        btnSignup.text = "Creating Account..."

        // Create user
        val result = userManager.createUser(
            name = name,
            email = email,
            phone = phone,
            password = password,
            photoPath = photoPath
        )

        result.onSuccess { user ->
            Toast.makeText(this, "Account created! 7-day free trial started!", Toast.LENGTH_LONG).show()
            
            // Auto-login
            userManager.login(email, password)
            
            // Navigate to dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }.onFailure { error ->
            btnSignup.isEnabled = true
            btnSignup.text = "Sign Up"
            Toast.makeText(this, "Signup failed: ${error.message}", Toast.LENGTH_LONG).show()
        }
    }
}

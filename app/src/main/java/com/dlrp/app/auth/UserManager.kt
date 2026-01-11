package com.dlrp.app.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import java.security.MessageDigest

/**
 * Manages user authentication and profile operations.
 */
class UserManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    private val TAG = "UserManager"
    
    companion object {
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    /**
     * Hash password using SHA-256.
     */
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Create new user account.
     */
    fun createUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        photoPath: String? = null,
        securityQuestion: String? = null,
        securityAnswer: String? = null
    ): Result<UserProfile> {
        return try {
            // Check if user already exists
            if (getUserByEmail(email) != null) {
                return Result.failure(Exception("User with this email already exists"))
            }
            
            val passwordHash = hashPassword(password)
            val trialStartDate = System.currentTimeMillis()
            
            val user = UserProfile(
                name = name,
                email = email,
                phone = phone,
                passwordHash = passwordHash,
                photoPath = photoPath,
                isPremium = false,
                trialStartDate = trialStartDate,
                securityQuestion = securityQuestion,
                securityAnswer = securityAnswer?.let { hashPassword(it) }
            )
            
            saveUser(user)
            Log.d(TAG, "User created successfully: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user", e)
            Result.failure(e)
        }
    }
    
    /**
     * Login user with email and password.
     */
    fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val user = getUserByEmail(email)
                ?: return Result.failure(Exception("User not found"))
            
            val passwordHash = hashPassword(password)
            if (user.passwordHash != passwordHash) {
                return Result.failure(Exception("Incorrect password"))
            }
            
            // Update last login
            val updatedUser = user.copy(lastLogin = System.currentTimeMillis())
            saveUser(updatedUser)
            setCurrentUser(updatedUser)
            
            Log.d(TAG, "User logged in: ${user.email}")
            Result.success(updatedUser)
        } catch (e: Exception) {
            Log.e(TAG, "Error during login", e)
            Result.failure(e)
        }
    }
    
    /**
     * Logout current user.
     */
    fun logout() {
        prefs.edit()
            .remove(KEY_CURRENT_USER)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
        Log.d(TAG, "User logged out")
    }
    
    /**
     * Get current logged-in user.
     */
    fun getCurrentUser(): UserProfile? {
        val json = prefs.getString(KEY_CURRENT_USER, null) ?: return null
        return try {
            userFromJson(json)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing user data", e)
            null
        }
    }
    
    /**
     * Check if user is logged in.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getCurrentUser() != null
    }
    
    /**
     * Update user profile.
     */
    fun updateUser(user: UserProfile): Result<UserProfile> {
        return try {
            saveUser(user)
            if (getCurrentUser()?.id == user.id) {
                setCurrentUser(user)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Reset password using security question.
     */
    fun resetPassword(email: String, securityAnswer: String, newPassword: String): Result<Boolean> {
        return try {
            val user = getUserByEmail(email)
                ?: return Result.failure(Exception("User not found"))
            
            val answerHash = hashPassword(securityAnswer)
            if (user.securityAnswer != answerHash) {
                return Result.failure(Exception("Incorrect security answer"))
            }
            
            val newPasswordHash = hashPassword(newPassword)
            val updatedUser = user.copy(passwordHash = newPasswordHash)
            saveUser(updatedUser)
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Activate premium subscription.
     */
    fun activatePremium(paymentId: String, durationDays: Int = 365): Result<UserProfile> {
        val user = getCurrentUser() ?: return Result.failure(Exception("No user logged in"))
        
        val expiryDate = System.currentTimeMillis() + (durationDays * 24 * 60 * 60 * 1000L)
        val updatedUser = user.copy(
            isPremium = true,
            premiumExpiryDate = expiryDate
        )
        
        return updateUser(updatedUser)
    }
    
    // Private helper methods
    
    private fun setCurrentUser(user: UserProfile) {
        val json = userToJson(user)
        prefs.edit()
            .putString(KEY_CURRENT_USER, json)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }
    
    private fun saveUser(user: UserProfile) {
        val json = userToJson(user)
        prefs.edit().putString("user_${user.email}", json).apply()
    }
    
    private fun getUserByEmail(email: String): UserProfile? {
        val json = prefs.getString("user_$email", null) ?: return null
        return try {
            userFromJson(json)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun userToJson(user: UserProfile): String {
        return JSONObject().apply {
            put("id", user.id)
            put("name", user.name)
            put("email", user.email)
            put("phone", user.phone)
            put("passwordHash", user.passwordHash)
            put("photoPath", user.photoPath)
            put("isPremium", user.isPremium)
            put("premiumExpiryDate", user.premiumExpiryDate)
            put("trialStartDate", user.trialStartDate)
            put("createdAt", user.createdAt)
            put("lastLogin", user.lastLogin)
            put("securityQuestion", user.securityQuestion)
            put("securityAnswer", user.securityAnswer)
        }.toString()
    }
    
    private fun userFromJson(json: String): UserProfile {
        val obj = JSONObject(json)
        return UserProfile(
            id = obj.getString("id"),
            name = obj.getString("name"),
            email = obj.getString("email"),
            phone = obj.getString("phone"),
            passwordHash = obj.getString("passwordHash"),
            photoPath = obj.optString("photoPath").takeIf { it.isNotEmpty() },
            isPremium = obj.getBoolean("isPremium"),
            premiumExpiryDate = obj.optLong("premiumExpiryDate").takeIf { it > 0 },
            trialStartDate = obj.optLong("trialStartDate").takeIf { it > 0 },
            createdAt = obj.getLong("createdAt"),
            lastLogin = obj.getLong("lastLogin"),
            securityQuestion = obj.optString("securityQuestion").takeIf { it.isNotEmpty() },
            securityAnswer = obj.optString("securityAnswer").takeIf { it.isNotEmpty() }
        )
    }
}

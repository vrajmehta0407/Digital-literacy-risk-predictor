package com.dlrp.app.core

import android.app.Application
import com.dlrp.app.detection.ScamRuleEngine
import com.dlrp.app.guardian.GuardianManager
import com.dlrp.app.storage.LocalPreferences
import com.dlrp.app.voice.VoiceAlertManager

class AppInitializer : Application() {

    companion object {
        lateinit var instance: AppInitializer
            private set
    }

    lateinit var preferences: LocalPreferences
    lateinit var scamRuleEngine: ScamRuleEngine
    lateinit var voiceAlertManager: VoiceAlertManager
    lateinit var guardianManager: GuardianManager
    lateinit var backgroundServiceManager: BackgroundServiceManager

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Core Components
        preferences = LocalPreferences(this)
        voiceAlertManager = VoiceAlertManager(this)
        guardianManager = GuardianManager(this, preferences)
        backgroundServiceManager = BackgroundServiceManager(this)
        
        // Initialize Risk Engine
        scamRuleEngine = ScamRuleEngine(this)
        
        // Setup channels etc.
    }
}

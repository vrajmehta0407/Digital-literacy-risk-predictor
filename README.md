# Safe Senior - Digital Literacy Risk Predictor

**Protecting elderly users from digital scams with intelligent, automatic safety features.**

## 🎯 Overview

Safe Senior is an Android application designed specifically for elderly users (55+) to protect them from SMS scams, fraudulent calls, and OTP phishing attacks. The app works silently in the background, requiring zero technical knowledge from the user.

## ✨ Key Features

### 🛡️ Automatic Scam Detection
- **Real-time SMS Analysis**: Detects scam patterns including urgency keywords, impersonation attempts, and suspicious links
- **OTP Protection**: Automatically masks risky OTP codes with a full-screen security overlay
- **Silent Call Handling**: Mutes unknown/suspicious calls and provides voice warnings
- **Context-Aware**: Distinguishes between legitimate messages (from banks) and scam attempts

### 👴 Elderly-Friendly Design
- **Zero Configuration**: Works automatically after initial setup
- **Large UI Elements**: Big buttons, high-contrast colors (28sp+ text)
- **Voice Alerts**: Loud, slow, simple voice warnings in critical situations
- **No Technical Jargon**: All messages use plain, reassuring language
- **One-Tap Panic Button**: Immediate help access

### 👨‍👩‍👧 Guardian Feature
- **Family Connection**: Link one trusted contact as a guardian
- **Smart Alerts**: Guardian notified only during high-risk events
- **Easy Setup**: Select guardian directly from phonebook

### 🔒 Privacy & Security
- **Offline-First**: Scam detection uses local rules, no data sent to servers
- **Screenshot Protection**: Security overlays prevent screenshots/screen recording
- **No Login Required**: No OTPs or passwords needed to use the app
- **Minimal Permissions**: Only requests essential Android permissions

## 🏗️ Technical Architecture

### Built With
- **Language**: Kotlin
- **Platform**: Android (API 24+, Target API 34)
- **Architecture**: MVVM pattern with singleton managers
- **Key Components**:
  - `ScamRuleEngine`: Multi-detector risk analysis system
  - `VoiceAlertManager`: Text-to-Speech safety warnings
  - `GuardianManager`: Family notification system
  - `SilentCallHandler`: Ringer control for suspicious calls

### Core Detection Modules
1. **KeywordDetector**: Identifies scam-related terms
2. **UrgencyDetector**: Flags fear-based language
3. **LinkAndCodeDetector**: Extracts and validates OTPs/URLs
4. **AuthorityImpersonationDetector**: Detects fake government/bank messages
5. **SuspiciousCallDetector**: Validates incoming call numbers

## 📱 How It Works

1. **Background Monitoring**: App listens for incoming SMS and calls
2. **Risk Analysis**: Each message/call is scored using multiple detectors
3. **Automatic Protection**:
   - **Low Risk**: No action
   - **Caution**: Voice warning
   - **Danger**: Full-screen security overlay + voice alert + guardian notification
4. **OTP Masking**: Risky OTPs displayed as `******` with security warnings

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version)
- Android SDK API 34
- Emulator or physical device (Android 7.0+)

### Installation
```bash
git clone https://github.com/yourusername/DigitalLiteracyRiskPredictor.git
cd DigitalLiteracyRiskPredictor
./gradlew build
```

### Running the App
1. Open project in Android Studio
2. Sync Gradle dependencies
3. Run on emulator (Pixel 7, API 34 recommended)
4. Grant permissions (SMS, Phone, Contacts, Do Not Disturb)
5. Set up a Guardian contact (optional)

### Testing Scam Detection
**Test SMS**: Send this message to the emulator:
```
Your OTP is 123456. Verify immediately to avoid account blocking.
```
**Expected Result**: Red security screen with masked code and voice warning

## 📂 Project Structure

```
app/src/main/java/com/dlrp/app/
├── calls/          # Call monitoring & silent mode
├── core/           # App initialization & permissions
├── detection/      # Scam detection engines
├── guardian/       # Family notification system
├── risk/           # Risk scoring & decision logic
├── sms/            # SMS analysis & receivers
├── storage/        # Local preferences
├── ui/             # Activities & UI components
└── voice/          # Text-to-Speech alerts
```

## 🎨 UI Screenshots

- **Home Screen**: Green status card, daily safety tip, guardian setup, panic button
- **Safety Alert**: Red overlay with masked OTP and security warnings
- **Guardian Setup**: Simple contact picker interface

## 🔐 Permissions Required

| Permission | Purpose |
|------------|---------|
| `RECEIVE_SMS` | Monitor incoming messages |
| `READ_SMS` | Analyze message content |
| `READ_PHONE_STATE` | Detect incoming calls |
| `READ_CONTACTS` | Verify trusted senders |
| `MODIFY_AUDIO_SETTINGS` | Silence suspicious calls |
| `ACCESS_NOTIFICATION_POLICY` | Enable Do Not Disturb mode |

## 🌍 Localization

Currently supports:
- English (default)
- Hindi (hi)
- Gujarati (gu)
- Marathi (mr)
- Tamil (ta)

## 🤝 Contributing

This project was built for a hackathon to address digital literacy challenges faced by elderly users in India. Contributions are welcome!

## 📄 License

[Add your license here]

## 👥 Authors

- Code Yodha
- Members Name :
- 1) Akshat Khatri
  2) Krish Patel
  3) Manan Gohil(Leader)
  4) Vraj Mehta

## 🙏 Acknowledgments

- Built with focus on accessibility and user safety
- Inspired by real-world scam incidents affecting elderly users
- Designed with input from senior citizens and their families

## 📞 Support

For issues or questions, please open a GitHub issue or contact [codeyodha09@gmail.com]

---

**⚠️ Disclaimer**: This app provides an additional layer of protection but should not be the only defense against scams. Users should always verify suspicious communications through official channels.

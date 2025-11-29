# 🛡️ VisionProtect

<div align="center">

![VisionProtect Logo](https://img.shields.io/badge/VisionProtect-AI%20Eye%20Safety-00F5FF?style=for-the-badge&logo=android&logoColor=white)

### *"Guard Your Vision, Protect Your Future"*

[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![ML Kit](https://img.shields.io/badge/ML%20Kit-FF6F00?style=flat-square&logo=google&logoColor=white)](https://developers.google.com/ml-kit)

**🏆 Built for MumbaiHacks 2025**

</div>

---

## 🌟 Overview

**VisionProtect** is an AI-powered Android application designed to protect your eyes in the digital age. Using advanced machine learning and real-time face detection, it monitors your screen usage habits and helps maintain healthy eye practices.

In today's world where we spend 8+ hours staring at screens, VisionProtect acts as your personal eye health guardian - detecting when you're too close, tracking your blink rate, analyzing posture, and providing actionable insights.

---

## ✨ Features

### 🔒 Smart Screen Protection
- **Auto Screen Block** when distance < 100cm
- **Warning Alerts** when distance < 200cm
- Real-time distance monitoring using front camera

### ��️ Blink Counter
- AI-powered blink detection
- Tracks blinks per minute
- Alerts for low blink rates (healthy: 15-20/min)

### 🧍 Body Posture Analysis
- Real-time posture monitoring
- Head position tracking
- Shoulder alignment detection

### 📏 Distance Monitor
- Continuous screen distance tracking
- Color-coded safety zones (Safe/Warning/Danger)
- Visual warnings when too close

### 📊 Daily Analytics
- Eye health score calculation
- Session history tracking
- Screen time monitoring
- Detailed usage statistics

### 👨‍👩‍👧 Parent Control
- Monitor children's screen time
- Set usage limits
- View activity reports

---

## 🎨 UI/UX

VisionProtect features a stunning **futuristic galaxy-themed UI** with:

- 🌌 Animated galaxy background with floating stars
- ✨ Neon gradient color scheme (Cyan, Purple, Pink)
- 🔮 Glowing animated components
- 💫 Smooth transitions and animations
- 🌙 Dark mode optimized design

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Modern declarative UI |
| **ML Kit Face Detection** | Real-time face & eye tracking |
| **CameraX** | Camera integration |
| **Navigation Compose** | Screen navigation |
| **Foreground Service** | Background monitoring |
| **Material 3** | Design system |

---

## 📱 Screenshots

<div align="center">

| Home Screen | Blink Counter | Distance Monitor | Analytics |
|:-----------:|:-------------:|:----------------:|:---------:|
| Galaxy UI | Real-time count | Safety zones | Daily stats |

</div>

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+
- Kotlin 1.9+

### Installation

1. **Clone the repository**
   `bash
   git clone https://github.com/yourusername/VisionProtect.git
   `

2. **Open in Android Studio**
   `bash
   cd VisionProtect/VisionProtect_1.0
   `

3. **Build and Run**
   - Connect your Android device
   - Enable USB debugging
   - Click Run ▶️

### Permissions Required
- 📷 Camera (for face detection)
- 🔔 Notifications (for alerts)
- 🖥️ Overlay (for screen protection)

---

## 🏗️ Architecture

`
VisionProtect/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/visionprotect04/
│   │   │   ├── MainActivity.kt
│   │   │   ├── service/
│   │   │   │   ├── VisionProtectService.kt
│   │   │   │   └── EyeHealthManager.kt
│   │   │   └── ui/screens/
│   │   │       ├── HomeScreen.kt
│   │   │       ├── CameraScreen.kt
│   │   │       ├── BlinkCounterScreen.kt
│   │   │       ├── BodyPostureScreen.kt
│   │   │       ├── DistanceMonitorScreen.kt
│   │   │       └── AnalyticsScreen.kt
│   │   └── res/
│   └── build.gradle
└── README.md
`

---

## 🎯 How It Works

1. **Face Detection**: ML Kit detects face position and eye states
2. **Distance Calculation**: Estimates screen distance from face size
3. **Blink Detection**: Monitors eye open probability changes
4. **Protection Layer**: Overlays warning/block screen when needed
5. **Analytics**: Aggregates data for health insights

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👥 Team

Built with 💜 for **MumbaiHacks 2025**

---

<div align="center">

### 🌟 Star this repo if VisionProtect helped you! 🌟

**Protect your eyes. Protect your future.**

![Made with Love](https://img.shields.io/badge/Made%20with-❤️-red?style=for-the-badge)

</div>

# MKSU Pamoja - Mental Health Support App

A comprehensive Android application designed to centralize mental health resources for Machakos University (MKSU) students.

## 🎯 Project Overview

MKSU Pamoja is a mobile mental health platform that provides:
- **Counseling Services** - Connect with professional counselors
- **Appointment Booking** - Schedule and manage counseling sessions
- **Resource Library** - Access mental health articles and guides
- **Community Support** - Connect with peers through forums
- **SMS Engagement** - Receive supportive messages and reminders
- **Self-Help Tools** - Interactive resources for mental wellness

## 🏗️ Architecture

- **Framework**: Android (Kotlin) with Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **Database**: Room (local) + Firebase Firestore (cloud)
- **Authentication**: Firebase Auth
- **Dependency Injection**: Hilt
- **UI**: Material 3 Design System

## 🚀 Current Status

### ✅ Completed Features
- Project structure and build configuration
- Firebase integration (Auth + Firestore)
- User authentication system (login/signup)
- Modern UI with Jetpack Compose
- Data models and repository layer
- Dependency injection setup
- Navigation framework

### 🔧 In Progress
- Authentication flow debugging (post-login crash)
- Home screen data loading optimization
- Counselor directory implementation

### 📋 Planned Features
- Appointment booking system
- Counselor availability management
- Resource library with categories
- Community forums
- SMS integration
- Push notifications

## 🛠️ Technical Stack

```
Frontend: Jetpack Compose + Material 3
Backend: Firebase (Auth, Firestore, Functions)
Local DB: Room Database
DI: Hilt
Navigation: Compose Navigation
State Management: StateFlow + Compose State
```

## 📁 Project Structure

```
app/src/main/java/com/mksu/pamoja/
├── data/
│   ├── dao/           # Room DAOs
│   ├── model/         # Data models
│   └── repository/    # Repository layer
├── di/                # Dependency injection modules
├── ui/
│   ├── screens/       # Compose screens
│   ├── theme/         # App theming
│   └── viewmodel/     # ViewModels
└── MainActivity.kt    # Entry point
```

## 🔥 Firebase Configuration

The app uses Firebase for:
- **Authentication**: Email/password login
- **Firestore**: User profiles and app data
- **Crashlytics**: Error reporting

## 🚧 Known Issues

1. **Build Performance**: Compilation takes excessive time on lower-end hardware
2. **Authentication Crash**: App crashes after successful login (under investigation)
3. **Kotlin Daemon**: Intermittent connection issues during compilation

## 🔧 Development Setup

1. Clone the repository
2. Open in Android Studio
3. Add your `google-services.json` file to `app/` directory
4. Sync project with Gradle files
5. Run on emulator or device

## 📊 Development Progress

See `plan.md` for detailed development roadmap and current status.

## 🤝 Contributing

This is an academic project for Machakos University. For collaboration or questions, please refer to the development plan.

## 📄 License

Educational project - Machakos University

---

**Note**: This project is actively being developed. The authentication system is functional but requires debugging for post-login navigation issues.

# Ivy Wallet Personal

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material3-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline-success.svg)](#privacy--offline-first)
[![APK Size](https://img.shields.io/badge/Release%20APK-~7.1%20MB-blueviolet.svg)](#performance--optimization)

**Ivy Wallet Personal** is a private, lightweight, and modern offline-first personal finance manager for Android. Built with 100% Kotlin and Jetpack Compose, it gives you complete ownership over your financial data with zero telemetry, zero ads, and zero server dependencies.

---

## ✨ Key Features

### 🔒 Privacy & Offline-First
- **Zero Cloud Tracking**: No analytics, no advertising SDKs, and no external tracking.
- **Local Database**: All data is securely stored on your device using SQLite (Room).
- **No Unwanted Online Sync**: No third-party exchange rate polling or network dependencies.
- **Biometric Security**: App lock with Fingerprint/Face Unlock and biometric protection.
- **Complete Data Ownership**: Backup and restore anytime with encrypted ZIP or export to CSV.

### 🎨 Deep Theming & Color Customization
- **Theme Modes**:
  - **Auto (System Default)**: Automatically adapts to your Android system's dark and light settings.
  - **Light Mode**: Crisp, high-contrast light theme for daytime use.
  - **Dark Mode**: Soft, eye-friendly charcoal theme for night.
  - **AMOLED True Black**: Pitch-black (`#000000`) background optimized for OLED displays and battery savings.
- **Multi-Color Accent Options**:
  - 🟢 **Ivy Emerald** (Signature Green)
  - 🟣 **Royal Violet** (Classic Purple)
  - 🔵 **Ocean Blue**
  - 🟠 **Sunset Orange**
  - 🌸 **Rose Berry**
  - 🔴 **Ruby Red**

### 📊 Powerful Money Management
- **Accounts & Wallets**: Manage multiple cash accounts, credit cards, bank accounts, and investments.
- **Insights & Quick Filters**: Filter transactions by All, Expense, Income, and Transfer with real-time monthly cashflow cards.
- **Budgets & Goals**: Set category-specific spending limits and monitor remaining budgets.
- **Planned & Recurring Payments**: Schedule subscriptions and recurring bills with upcoming payment reminders.
- **Loans & Debts**: Track money lent to and borrowed from contacts with repayment logs.
- **Categories**: Fully customizable categories with color palettes and custom icons.

### ⚡ Performance & Optimization
- **Ultra-Compact Size**: Shrunk from legacy 35MB+ builds down to **~7.1 MB** release APK through R8 full-mode optimization, resource shrinking, and unused font stripping.
- **Smooth 120Hz Animations**: Fluid Jetpack Compose animations and instant screen transitions.

---

## 🛠️ Architecture & Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Material 3](https://m3.material.io/)
- **Language**: 100% [Kotlin](https://kotlinlang.org/) with Coroutines & StateFlow
- **Dependency Injection**: [Dagger Hilt](https://dagger.dev/hilt/)
- **Local Storage**: [Room Database](https://developer.android.com/training/data-storage/room) & [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) / SharedPreferences
- **Build System**: Gradle Kotlin DSL with version catalogs and automated GitHub Actions CI

---

## 🚀 Building & Running

### Requirements
- **JDK 17** (Temurin or OpenJDK recommended)
- **Android SDK** with target API 34+

### Build Commands

```bash
# Clone the repository
git clone https://github.com/NaveenKumarYeluri/ivy-wallet-personal.git
cd ivy-wallet-personal

# Build Debug APK
./gradlew assembleDebug

# Build Optimized Release APK (R8 full mode, resource shrinking)
./gradlew assembleRelease
```

The compiled APKs will be located at:
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

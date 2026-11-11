# Ivy Wallet Personal

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material3-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline-success.svg)](#-privacy--offline-first)
[![APK Size](https://img.shields.io/badge/Release%20APK-~7.1%20MB-blueviolet.svg)](#-performance--optimization)

**Ivy Wallet Personal** is an offline-first, privacy-focused, and modern personal finance manager for Android. Built with 100% Kotlin and Jetpack Compose, it gives you complete ownership over your financial data with zero telemetry, zero ads, and zero server dependencies.

---

## 📢 Attribution & Upstream Project

This project is a customized personal fork based upon the original open-source [**Ivy Wallet**](https://github.com/Ivy-Apps/ivy-wallet) project created by [**Iliyan Germanov**](https://github.com/iliyangermanov) and the [Ivy Apps](https://github.com/Ivy-Apps) community.

- **Original Repository**: [https://github.com/Ivy-Apps/ivy-wallet](https://github.com/Ivy-Apps/ivy-wallet)
- **Original License**: [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0)
- **Copyright**: © 2021–present Ivy Apps & original contributors.

We are deeply grateful to Iliyan Germanov and all the open-source contributors who designed and developed the original Ivy Wallet application. In accordance with the GNU General Public License v3.0, all enhancements and modifications in this repository remain fully open source under the GPLv3 license.

---

## ✨ Features & Enhancements

### ⚡ Quick Log on Homescreen
- **1-Tap Fast Logging**: Log frequent daily expenses with a single tap directly from the Home screen.
- **Customizable Templates**: Add, edit, and delete your own personalized Quick Log templates with custom title, category, payment account, amount, and transaction type (Expense/Income).
- **Default & User Presets**: Comes with standard presets (Coffee, Lunch, Groceries) plus full freedom to manage your own.

### 🔒 Privacy & Offline-First
- **100% Offline**: No network permissions needed for tracking, no telemetry, no tracking SDKs, and no ads.
- **Local Room Database**: All data is stored securely on your local device.
- **Fixed Currency Simplicity**: Removed external currency converters and exchange rate network scrapers for an authentic, reliable fixed-currency budgeting experience.
- **Biometric App Lock**: Secure your finances with Fingerprint / Face Unlock.
- **Full Data Backup & Restore**: Export and import your data anytime via encrypted backups or CSV files.

### 🎨 Material 3 & Dynamic Theming
- **Adaptive Dark / Light Themes**:
  - **Auto (System Default)**: Adapts to system display mode.
  - **Light Mode**: Crisp, clean daytime interface.
  - **Dark Mode**: Gentle dark grey interface.
  - **AMOLED True Black**: Pitch black (`#000000`) for maximum OLED battery savings.
- **Accent Color Palettes**:
  - 🟢 **Ivy Emerald** (Signature Green)
  - 🟣 **Royal Violet** (Classic Purple)
  - 🔵 **Ocean Blue**
  - 🟠 **Sunset Orange**
  - 🌸 **Rose Berry**
  - 🔴 **Ruby Red**
- **Cohesive Semantic Theming**: Dynamic theme integration across Income, Expense, Day Totals, Transaction Lists, Date Dividers, and Statistic Charts.

### ⚙️ Enhanced Advanced Features
- **Hide Account Total Balance**:
  - Masks individual account card balances with `****` for privacy in public.
  - **Tap-to-Peek**: Tap any masked balance on an account card to temporarily reveal it; tap again to hide.
  - Masks the Home screen Total Balance card (with 5-second peek timer on tap).
- **Alphabetical Category Sorting**:
  - Case-insensitive (A-Z) alphabetical category sorting across the Categories screen, Transaction Entry screen, Planned Payments, and Quick Log templates.
- **Compact UI Modes**: Dense view options for both Accounts and Categories screens.
- **Category Search**: Instant search bar to quickly find categories.

### 📊 Comprehensive Money Management
- **Accounts & Wallets**: Manage cash, bank accounts, credit cards, investments, and custom assets.
- **Budgets & Spending Limits**: Set monthly category budgets with visual progress indicators.
- **Planned & Recurring Bills**: Schedule recurring subscriptions and future payments.
- **Loans & Debts**: Track money borrowed and lent with repayment tracking.
- **Detailed Insights & Analytics**: Visual pie charts, category breakdowns, and monthly cash flow metrics.

### 🚀 Performance & Optimization
- **Ultra-Compact Size**: Reduced release APK size down to **~7.1 MB** (down from legacy 35MB+ builds) via R8 full-mode shrinking, ProGuard optimizations, and unused resource stripping.
- **Modern Android & Tooling**: Built on target API 34+ with modern Gradle 8.11.1 and automated GitHub Actions CI.

---

## 🛠️ Architecture & Tech Stack

| Layer | Technologies |
|---|---|
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Material 3](https://m3.material.io/) |
| **Language** | 100% [Kotlin](https://kotlinlang.org/) with Coroutines & StateFlow |
| **Architecture** | Clean Architecture + MVI / FRP pattern with Compose ViewModels |
| **Dependency Injection** | [Dagger Hilt](https://dagger.dev/hilt/) |
| **Persistence** | [Room Database](https://developer.android.com/training/data-storage/room) & [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) |
| **Build System** | Gradle 8.11.1 (Kotlin DSL `.kts`) + GitHub Actions CI |

---

## 🚀 Building & Releasing

### Prerequisites
- **JDK 17** (Temurin or OpenJDK recommended)
- **Android SDK** with build-tools and API level 34+

### Clone & Build

```bash
# Clone the repository
git clone https://github.com/NaveenKumarYeluri/ivy-wallet-personal.git
cd ivy-wallet-personal

# Build Debug APK
./gradlew assembleDebug

# Build Optimized Release APK (R8 full-mode optimization)
./gradlew assembleRelease
```

### Build Artifacts
Compiled APKs are output to:
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`

Automated GitHub Actions workflows also compile and upload both artifacts on every push to `main`.

---

## 📄 License & Legal Notice

This software is licensed under the **[GNU General Public License v3.0](LICENSE)**.

```
Ivy Wallet Personal - Offline-first personal finance manager
Copyright (C) 2021-present Ivy Apps and original contributors
Copyright (C) 2026 Naveen Kumar Yeluri and contributors

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
```

Original Ivy Wallet source code: [https://github.com/Ivy-Apps/ivy-wallet](https://github.com/Ivy-Apps/ivy-wallet)

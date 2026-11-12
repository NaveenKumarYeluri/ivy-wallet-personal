# Ivy Wallet Personal

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Latest Release](https://img.shields.io/github/v/release/NaveenKumarYeluri/ivy-wallet-personal?label=Release&color=success)](https://github.com/NaveenKumarYeluri/ivy-wallet-personal/releases/latest)
[![Android](https://img.shields.io/badge/Platform-Android%2015%2B-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material3-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline-success.svg)](#-privacy--offline-first)
[![APK Size](https://img.shields.io/badge/Release%20APK-~7.2%20MB-blueviolet.svg)](#-performance--optimization)

**Ivy Wallet Personal** is an offline-first, privacy-focused, and modern personal finance manager for Android. Built with 100% Kotlin and Jetpack Compose, it gives you complete ownership over your financial data with zero telemetry, zero ads, and zero server dependencies.

---

## 📢 Attribution & Upstream Project

This project is a customized personal fork based upon the original open-source [**Ivy Wallet**](https://github.com/Ivy-Apps/ivy-wallet) project created by [**Iliyan Germanov**](https://github.com/iliyangermanov) and the [Ivy Apps](https://github.com/Ivy-Apps) community.

- **Original Repository**: [https://github.com/Ivy-Apps/ivy-wallet](https://github.com/Ivy-Apps/ivy-wallet)
- **Original License**: [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0)
- **Copyright**: © 2021–present Ivy Apps & original contributors.

We are deeply grateful to Iliyan Germanov and all open-source contributors who designed and developed the original Ivy Wallet application. In accordance with the GNU General Public License v3.0, all enhancements and modifications in this repository remain fully open source under the GPLv3 license.

### 🤖 AI Pair Programming Attribution

Ivy Wallet Personal has been modernized, enhanced, and engineered with the continuous pair-programming assistance of **Google Antigravity** (Google DeepMind Advanced Agentic AI).

Key contributions developed with AI assistance include:
- **Interactive Reports Navigation & Filtering**: Designing Month/Year direct picker modals, full multi-criteria optional filter validation, dynamic active filter cards, and balanced income/spending category breakdowns.
- **Homescreen Quick Log System**: Designing and implementing 1-tap quick logging with full custom template creation, editing, and persistent storage.
- **Material 3 Dynamic Theming**: Overhauling legacy hardcoded green/red colors into dynamic, theme-adaptive semantic colors for income, expense, date dividers, and statistics charts.
- **Privacy & Settings Fixes**: Implementing account total balance masking with tap-to-peek privacy across account cards and Home screen, along with case-insensitive alphabetical category sorting.
- **Architecture & Optimization**: Streamlining offline fixed-currency workflows, removing obsolete network polling, shrinking APK size to ~7.2 MB with R8 full-mode optimization, and modernizing Gradle CI/CD pipelines.

---

## ✨ Features & Enhancements

### ⚡ Quick Log on Homescreen
- **1-Tap Fast Logging**: Log frequent daily expenses with a single tap directly from the Home screen.
- **Customizable Templates**: Add, edit, and delete your own personalized Quick Log templates with custom title, category, payment account, amount, and transaction type (Expense/Income).
- **Default & User Presets**: Comes with standard presets (Coffee, Lunch, Groceries) plus full freedom to manage your own.

### 📊 Advanced Financial Reports & Analytics
- **Interactive Month & Year Quick Pickers**:
  - Direct modal picker dialogs for Month-Year and Year views.
  - Jump immediately to any past or future period without having to click left/right arrows dozens of times.
- **Flexible Custom Filter Engine**:
  - Filter by any combination of transaction types (Expense, Income, Transfer), time periods (including All Time), accounts, categories, amounts, tags, and keywords.
  - Filter criteria are truly optional—filter transactions for a specific account without being forced to pick a category.
- **Active Filter Management**:
  - Context-aware UI automatically hides redundant period tabs ("Monthly, Yearly, All Time") and navigation arrows when a custom filter is in effect.
  - Prominent **Active Filter Card** showing current filter status and 1-tap **"Edit"** and **"Clear filter"** controls.
  - Toolbar active filter indicator badge.
- **Visual Analytics**:
  - **Cash Flow Ratio Bar**: Real-time visual comparison of Total Income vs Total Expenses with animated proportional tracking.
  - **Savings Efficiency**: Dynamic tracking of savings percentage (`% retained from income`) and surplus/deficit metrics.
  - **Category Breakdown**: Top spending and income category breakdowns with transaction counts, spend totals, percentage share, and category-themed progress tracks.
  - **Daily Spending Heatmap**: Visual month-at-a-glance spending density calendar.
  - **Shareable Financial Summary**: Generate formatted executive financial statements ready to share directly via the system share sheet.

### 📅 Recurring Bills & Subscriptions Calendar
- **Dual Mode Switcher**: Seamlessly toggle between standard List View and the interactive Calendar View.
- **Monthly Calendar Grid**: Month-by-month navigation with visual badges and color dots indicating upcoming due bills.
- **Expected Monthly Summary**: Instant overview of expected monthly income, expected bills, and projected net balance.
- **Day-by-Day Inspection**: Tap any date on the calendar to see all subscriptions and planned payments due on that exact day.

### 🔒 Privacy & Offline-First
- **100% Offline**: No network permissions needed for tracking, zero telemetry, zero analytics SDKs, and zero ads.
- **Local Room Database**: All data is stored securely on your local device.
- **Fixed Currency Simplicity**: Removed external currency converters and exchange rate network scrapers for an authentic, reliable fixed-currency budgeting experience.
- **Biometric App Lock**: Secure your finances with Fingerprint / Face Unlock.
- **Full Data Backup & Restore**: Export and import your data anytime via encrypted backups or CSV files.

### 🛡️ AES-256 Encrypted Local Vault Backup
- **Cryptographic Security**: Export `.ivybkp` vault files protected with **AES-256-GCM** authenticated encryption.
- **PBKDF2 Key Derivation**: High-security password hashing (`PBKDF2WithHmacSHA256`, 65,536 iterations, 16-byte cryptographic salt).
- **Tamper Detection**: GCM authentication tags guarantee data integrity and immediately detect invalid passwords or corrupted files.
- **100% Offline Vault**: Safely store encrypted backups on external drives, cloud storage, or USB without exposing sensitive personal ledger data.

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

### ⚙️ Enhanced Usability & Controls
- **Hide Account Total Balance**:
  - Masks individual account card balances with `****` for privacy in public.
  - **Tap-to-Peek**: Tap any masked balance on an account card to temporarily reveal it; tap again to hide.
  - Masks the Home screen Total Balance card (with 5-second peek timer on tap).
- **Alphabetical Category Sorting**:
  - Case-insensitive (A-Z) alphabetical category sorting across Categories screen, Transaction Entry, Planned Payments, and Quick Log templates.
- **Dense / Compact View Modes**: Optional compact layouts for both Accounts and Categories screens.
- **Instant Search**: Rapid search filtering across Categories and Transactions.

### 🚀 Performance & Optimization
- **Ultra-Lean Footprint**: Reduced release APK size down to **~7.2 MB** (down from legacy 35MB+ builds) via R8 full-mode shrinking, ProGuard optimizations, and unused resource stripping.
- **Modern Android & Tooling**: Built on target API 35/36 (Android 15+) with modern Gradle 8.11.1 and automated GitHub Actions CI.

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
- **Android SDK** with build-tools and API level 35+

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

Automated GitHub Actions workflows also compile and upload both artifacts on every push to `main`. Pre-compiled APKs are available on the [Releases](https://github.com/NaveenKumarYeluri/ivy-wallet-personal/releases) page.

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

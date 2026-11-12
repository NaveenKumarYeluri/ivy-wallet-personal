# Contributing to Ivy Wallet Personal

Thank you for your interest in contributing to **Ivy Wallet Personal**! This repository is an offline-first, modernized personal edition of Ivy Wallet.

## Development Workflow

1. **Fork and Clone**:
   ```bash
   git clone https://github.com/NaveenKumarYeluri/ivy-wallet-personal.git
   cd ivy-wallet-personal
   ```

2. **Branching**:
   Create a dedicated branch for your feature or bug fix:
   ```bash
   git checkout -b feature/my-enhancement
   ```

3. **Coding Standards & Architecture**:
   - Modern Android architecture with Clean Architecture + MVI/FRP patterns.
   - 100% Kotlin with Jetpack Compose & Material 3.
   - Keep the app **100% offline**: no network tracking SDKs, no ads, and no external telemetry.
   - Run Detekt and verify builds pass before opening a Pull Request.

4. **Building & Testing**:
   ```bash
   # Build debug APK
   ./gradlew assembleDebug

   # Build optimized release APK
   ./gradlew assembleRelease
   ```

5. **Submitting a Pull Request**:
   - Open a pull request against the `main` branch of this repository.
   - Provide a clear summary of your changes, the rationale, and screenshots/videos if UI changes were introduced.

## License

By contributing to this repository, you agree that your contributions will be licensed under the [GNU General Public License v3.0](LICENSE).

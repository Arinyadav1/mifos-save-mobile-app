# Mifos Save Mobile App — Antigravity Agent Rules

This file guides the Google Antigravity AI agent in development, testing, and formatting tasks for the Mifos Save Mobile App project.

## 🚀 Build & Test Commands

Use the following commands to check, build, and test the project:

- **Code Formatting:**
  `./gradlew spotlessApply`
- **Static Analysis & Code Quality Checks:**
  `./gradlew check spotlessCheck detekt dependencyGuard`
- **Run All Tests:**
  `./gradlew test`
- **Build All Platforms (Debug):**
  `./gradlew assembleDebug build`
- **Build Android Release:**
  `./gradlew :cmp-android:assembleRelease`
- **Build Desktop Release:**
  `./gradlew packageReleaseDistributionForCurrentOS`
- **Build Web Release:**
  `./gradlew jsBrowserDistribution`

---

## ⚠️ Key Constraints & Security

- **Secrets Management:**
  - **NEVER** commit files in `secrets/`, `keystores/`, or files matching `*.keystore`, `*.p8`, `*.p12`, `.env`.
  - Use `keystore-manager.sh` for managing keys and secrets.
- **Git Branch Protection:**
  - **NEVER** commit directly to `master` or `dev`.
  - Always develop on a feature branch (e.g., `feature/your-feature`) and merge via a PR.
- **Production Deployments:**
  - App Store and Play Store production deployments must be run manually via GitHub Actions with double confirmation. Do not run production deployment fastlane tasks directly.

---

## 🛠️ Codebase Architecture & Guidelines

- **Kotlin Multiplatform (KMP):** The project spans Android, iOS, macOS, Desktop (JVM), and Web (JS).
- **Core Seam (`core/store`):**
  - Brand customization, domain UIs, and app defaults reside in `core/store`.
  - Customize `AppScreenStateDefaults`, `AppErrorMapper`, `AppStoreRegistry`, and `appStoreModule`.
  - **Do NOT** modify `core-base/store` or `core-base/ui` directly as they are shared, upgradable framework modules.
- **Compose Multiplatform UI:**
  - Use `PagingScreenContent` for infinite-scroll paginated lists.
  - Use `ScreenContent` for details or non-paginated lists.
  - Use `MutationScreenContent` along with `SubmitHandler` or `DraftSubmitHandler` for inputs/forms.

# StudyBuddy

An Android study assistant app with an AI chat backed by Groq's LLM API, OCR scanner input, study-session history, and per-key settings.

## Features

- **AI Chat** — Streaming-style chat UI powered by Groq (default model `llama-3.1-8b-instant`).
- **Scanner** — Capture text via the camera or pick from the gallery (CameraX + ML Kit OCR integration planned).
- **History** — Browse, filter, and export past study sessions as PDF.
- **Settings** — Persist your Groq API key locally (DataStore).

## Tech stack

- **Language:** Kotlin 1.9.24
- **UI:** Jetpack Compose (BOM `2024.04.00`), Material 3
- **Architecture:** MVVM — `ViewModel` + `StateFlow` / `Flow` collected with `collectAsStateWithLifecycle`
- **DI:** Hilt 2.50
- **Persistence:** Room 2.6.1
- **Async:** Kotlin Coroutines + Flow
- **Min/Target SDK:** `minSdk` 24 / `compileSdk` 34
- **Build tools:** AGP 8.6.0, Gradle, JDK 17

## Build

This is an Android Studio / Gradle project. From the project root:

```bash
# Debug APK
./gradlew assembleDebug --no-daemon

# Lint
./gradlew lintDebug --no-daemon
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## CI

GitHub Actions (`.github/workflows/android.yml`) runs `assembleDebug` + `lintDebug` on every push to and PR against `main`, then uploads the APK and lint report as artifacts.

Requires JDK 17 + Android SDK 34, both set up by the workflow.

## Configuration

Add your Groq API key in **Settings** (it's persisted on-device, never committed).

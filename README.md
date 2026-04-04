# 📖 Lexicon — Modern-Day Dictionary

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="120" alt="Lexicon App Icon"/>
</p>

<p align="center">
  <b>A clean, fast, and beautifully designed English dictionary for Android.</b><br/>
  Look up definitions, examples, synonyms, antonyms and hear pronunciations — all in one place.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen?logo=android" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue" />
  <img src="https://img.shields.io/badge/Target%20SDK-35-blue" />
  <img src="https://img.shields.io/badge/Version-1.1-purple" />
  <img src="https://img.shields.io/badge/Language-Kotlin-orange?logo=kotlin" />
  <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-red" />
</p>

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🔍 **Real-time Search** | Instant word suggestions as you type, powered by the Datamuse API |
| 📖 **Word Detail** | Full definitions, phonetics, part-of-speech, examples, synonyms & antonyms |
| 🔊 **Pronunciation** | Text-to-Speech playback for any word with one tap |
| ⭐ **Save Words** | Bookmark words locally with Room — swipe left to delete with undo |
| 📅 **Word of the Day** | A curated word on the home screen to grow your vocabulary daily |
| 📈 **Trending Words** | Discover trending words with a ranked list on the home screen |
| 🕐 **Recent Words** | Quick chip-based access to your last 10 searched words |
| 🌙 **Dark / Light Mode** | Toggle dark mode from Settings — persisted across sessions via DataStore |
| 📤 **Share** | Share any word's definition instantly via the Android share sheet |

---

## 📸 Screens

| Home | Search | Word Detail | Saved | Settings |
|------|--------|-------------|-------|----------|
| Discover Words + WOTD + Trending | Real-time suggestions | Full entry with chips | Saved list with swipe-to-delete | Dark mode toggle + preferences |

---

## 🏗️ Architecture

The app follows **Clean Architecture** with a strict separation of concerns across three layers:

```
┌─────────────────────────────────────────────────────┐
│                  UI Layer (MVVM)                     │
│  Fragments · ViewModels · Adapters · Widgets         │
└────────────────────┬────────────────────────────────-┘
                     │ StateFlow
┌────────────────────▼────────────────────────────────┐
│               Domain Layer                           │
│  UseCases · Repository Interfaces · Domain Models    │
└────────────────────┬────────────────────────────────-┘
                     │ suspend fun / Flow
┌────────────────────▼────────────────────────────────┐
│               Data Layer                             │
│  Retrofit DTOs · Room DB · DataStore · Mappers       │
└─────────────────────────────────────────────────────┘
```

### Key patterns
- **MVVM** — ViewModels expose `StateFlow` consumed by Fragments via `lifecycleScope.launch`
- **Hilt** — Constructor injection throughout; `@HiltViewModel`, `@Singleton`, `@Module`
- **Repository pattern** — Domain layer talks to interfaces; Data layer provides implementations
- **Use-cases** — One class per business action (`GetWordInfoUseCase`, `SaveWordUseCase`, …)

---

## 🛠️ Tech Stack

| Category | Library / Tool |
|----------|----------------|
| Language | [Kotlin](https://kotlinlang.org/) 1.9.x |
| UI | Views + ViewBinding, Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | [Hilt](https://dagger.dev/hilt/) 2.48 |
| Navigation | [Navigation Component](https://developer.android.com/guide/navigation) + Safe Args |
| Networking | [Retrofit](https://square.github.io/retrofit/) 2.9 + OkHttp 4.12 + Gson |
| Local DB | [Room](https://developer.android.com/training/data-storage/room) 2.6 |
| Preferences | [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) 1.1 |
| TTS | Android `TextToSpeech` API (via `TtsManager`) |
| Logging | [Timber](https://github.com/JakeWharton/timber) |
| Static Analysis | [Detekt](https://detekt.dev/) 1.23 with auto-correct |
| Build | AGP 8.6.1 · Gradle 8.7 · KSP |
| CI / Release | [Fastlane](https://fastlane.tools/) |

---

## 📡 APIs Used

| API | Purpose | Docs |
|-----|---------|------|
| [Dictionary API](https://dictionaryapi.dev/) | Word definitions, phonetics, examples, synonyms, antonyms | Free, no key |
| [Datamuse API](https://www.datamuse.com/api/) | Real-time word search suggestions | Free, no key |

---

## 📂 Project Structure

```
Dictionary-Application-MVVM/
├── app/src/main/
│   ├── java/labs/creative/dictornarymvvm/
│   │   ├── MainApplication.kt          # Hilt + Timber init
│   │   ├── core/
│   │   │   └── TtsManager.kt           # Text-to-Speech wrapper (Singleton)
│   │   ├── data/
│   │   │   ├── local/                  # Room DB, DAOs, entities
│   │   │   ├── preferences/            # DataStore repository
│   │   │   └── remote/                 # Retrofit services, DTOs
│   │   ├── di/                         # Hilt modules (Network, DB, DataStore, Repository)
│   │   ├── domain/
│   │   │   ├── mappers/                # DTO → Domain mappers
│   │   │   ├── model/                  # WordInfo, SavedWord, WordSuggestion
│   │   │   ├── repository/             # Repository interfaces
│   │   │   └── usecase/                # Business logic use-cases
│   │   └── ui/
│   │       ├── MainActivity.kt         # Single-activity host, dark mode init
│   │       ├── adapter/                # RecyclerView adapters (ListAdapter + DiffUtil)
│   │       ├── fragments/              # Main, Search, Result, Saved, Settings
│   │       ├── viewmodel/              # One ViewModel per screen
│   │       └── widgets/                # TypeWriterView custom view
│   └── res/
│       ├── layout/                     # XML layouts (ViewBinding)
│       ├── navigation/                 # nav_graph.xml with Safe Args
│       ├── values / values-night/      # Light + Dark theme color tokens
│       └── drawable / mipmap-*/        # Icons + launcher assets
├── docs/                               # Architecture, design & task docs
├── fastlane/                           # Release automation
└── gradle/libs.versions.toml           # Version catalog
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 17+**
- Android SDK with API 35 installed

### Run locally

```bash
# 1. Clone the repo
git clone https://github.com/Srujan-github/Dictionary-Application-MVVM.git
cd Dictionary-Application-MVVM

# 2. Open in Android Studio and let Gradle sync
# No API keys required — both APIs are open and free

# 3. Run on a device or emulator (API 24+)
./gradlew installDebug
```

### Build release AAB

```bash
./gradlew bundleRelease
```

---

## 🧪 Running Tests

```bash
# Unit tests
./gradlew test

# Static analysis (Detekt)
./gradlew detekt
```

---

## 📦 Release

This project uses **Fastlane** for automated Play Store deployment.

```bash
# Deploy to internal testing track
bundle exec fastlane deploy_internal
```

> See [`fastlane/`](./fastlane/) for lane configuration.

---

## 🎨 Design System

| Token | Light | Dark |
|-------|-------|------|
| Background | `#F5F0FF` | `#0F0B1E` |
| Surface | `#FAF8FF` | `#1C1635` |
| Primary | `#5C3FBE` | `#9B7FF5` |
| Text Primary | `#1A1A2E` | `#E8E0FF` |
| Text Secondary | `#5C5C7A` | `#A89ED4` |

Font: **Roboto** (system default) · Corner radius: 12 dp cards, 28 dp pills

---

## 📋 Changelog

### v1.1
- Bumped `targetSdk` to **API 35** (required by Play Console)
- Added native debug symbols (`ndk.debugSymbolLevel = FULL`) for crash symbolication
- Fixed dark / light mode theme switching
- Custom app icon (Lexicon book motif)
- Upgraded AGP to 8.6.1

### v1.0
- Initial release
- Home, Search, Word Detail, Saved Words, Settings screens
- Dark mode support
- Room persistence + DataStore preferences
- TTS pronunciation

---

## 📄 License

```
Copyright 2024 Sai Srujana Thammishetti

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

---

<p align="center">Made with ❤️ and Kotlin</p>

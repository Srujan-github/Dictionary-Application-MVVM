# 📚 Dictionary Application — Documentation Hub

> **App Name:** Lexicon — Dictionary MVVM  
> **Version:** 1.0.0  
> **Platform:** Android (minSdk 24 · targetSdk 34)  
> **Language:** Kotlin  
> **Architecture:** MVVM + Clean Architecture  

---

## 📂 Documentation Index

| # | Document | Description |
|---|----------|-------------|
| 1 | [Design Guidelines](design/DESIGN_GUIDELINES.md) | Color palette, typography, spacing, UI components & design language |
| 2 | [Architecture Guidelines](architecture/ARCHITECTURE_GUIDELINES.md) | Clean Architecture layers, MVVM pattern, data flow & DI wiring |
| 3 | [Coding Guidelines](coding/CODING_GUIDELINES.md) | Kotlin style, naming conventions, coroutines, error handling & Detekt |
| 4 | [Task — Home Screen](tasks/TASK_HOME_SCREEN.md) | Implementation plan for the Home / Discover Words screen |
| 5 | [Task — Word Detail](tasks/TASK_WORD_DETAIL.md) | Implementation plan for the Word Detail (Explore) screen |
| 6 | [Task — Saved Words](tasks/TASK_SAVED_WORDS.md) | Implementation plan for the Saved Words screen |
| 7 | [Task — Settings](tasks/TASK_SETTINGS.md) | Implementation plan for the Settings screen |
| 8 | [Task — Search](tasks/TASK_SEARCH.md) | Implementation plan for the real-time Search feature |

---

## 🗺️ Quick Project Overview

```
Dictionary-Application-MVVM/
├── app/
│   └── src/main/
│       ├── java/labs/creative/dictornarymvvm/
│       │   ├── core/           # Base classes & utilities
│       │   ├── data/           # Remote DTOs, API services
│       │   ├── di/             # Hilt DI modules
│       │   ├── domain/         # Models, repository contracts, use-cases, mappers
│       │   └── ui/             # Activities, Fragments, ViewModels, Adapters, Widgets
│       └── res/                # Layouts, drawables, navigation, themes
└── docs/                       # ← YOU ARE HERE
    ├── design/
    ├── architecture/
    ├── coding/
    └── tasks/
```

---

## 🚀 Getting Started

1. Clone the repository.
2. Open in **Android Studio Hedgehog** or later.
3. Sync Gradle — all dependencies resolve via `libs.versions.toml`.
4. Run on an emulator or physical device (API 24+).
5. No API keys needed; both APIs are open and free.

---

*Last updated: February 2026*

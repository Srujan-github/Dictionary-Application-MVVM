# ⚙️ Task — Settings Screen

> **Screen Name:** Settings  
> **Fragment:** `SettingsFragment` *(to be created)*  
> **Layout:** `fragment_settings.xml` *(to be created)*  
> **ViewModel:** `SettingsViewModel` *(to be created)*  
> **Persistence:** Jetpack DataStore (Preferences)  
> **Status:** 🔴 Not Started

---

## 1. Overview

The **Settings screen** lets users personalise their Lexicon experience. It is divided into two groups:

- **PREFERENCES** — Dark Mode toggle, Language selection, Daily Word notification toggle.
- **ABOUT** — App info links, Privacy Policy, Send Feedback, Rate the App.

All preferences are persisted with **Jetpack DataStore** (Preferences API) so they survive app restarts.

---

## 2. Visual Reference

```
┌──────────────────────────────────────┐
│  Settings                            │  ← Headline Large
│  Customize your experience           │  ← Subtitle
│                                      │
│  PREFERENCES                         │  ← Section label (ALL CAPS, accent)
│  ╔════════════════════════════════╗   │
│  ║  🌙  Dark Mode         [●○]  ║   │  ← Toggle (currently OFF)
│  ║       Reduce eye strain…     ║   │
│  ╠════════════════════════════════╣   │
│  ║  🌐  Language          [→]   ║   │  ← Tap to choose language
│  ║       English                ║   │
│  ╠════════════════════════════════╣   │
│  ║  🔔  Daily Word        [○●]  ║   │  ← Toggle (currently ON)
│  ║       Get a new word every…  ║   │
│  ╚════════════════════════════════╝   │
│                                      │
│  ABOUT                               │  ← Section label
│  ╔════════════════════════════════╗   │
│  ║  ℹ️   About Lexicon        →  ║   │
│  ╠════════════════════════════════╣   │
│  ║  🛡️   Privacy Policy      →  ║   │
│  ╠════════════════════════════════╣   │
│  ║  💬  Send Feedback        →  ║   │
│  ╠════════════════════════════════╣   │
│  ║  ❤️   Rate the App        →  ║   │
│  ╚════════════════════════════════╝   │
│                                      │
│            Lexicon v1.0.0            │  ← Version label (Body Medium, centered)
│                                      │
│  [Search] [Explore] [Saved] [⚙●]   │  ← Bottom Navigation
└──────────────────────────────────────┘
```

---

## 3. Settings Items

### 3.1 Preferences Group

#### Dark Mode

| Attribute | Value |
|-----------|-------|
| Icon | `dark_mode` |
| Title | "Dark Mode" |
| Subtitle | "Reduce eye strain at night" |
| Control | `SwitchMaterial` toggle |
| Persistence | DataStore key: `"dark_mode_enabled"` |
| Default | `false` (Light mode) |
| On Change | Apply `AppCompatDelegate.setDefaultNightMode()` |

#### Language

| Attribute | Value |
|-----------|-------|
| Icon | `language` |
| Title | "Language" |
| Subtitle | Current language name (e.g., "English") |
| Control | Tap → `AlertDialog` or bottom sheet with language list |
| Persistence | DataStore key: `"app_language"` |
| Default | `"en"` (English) |
| Supported | English (MVP); expandable later |

#### Daily Word Notification

| Attribute | Value |
|-----------|-------|
| Icon | `notifications` |
| Title | "Daily Word" |
| Subtitle | "Get a new word every day" |
| Control | `SwitchMaterial` toggle |
| Persistence | DataStore key: `"daily_word_enabled"` |
| Default | `true` (enabled) |
| On Change | Schedule / cancel `WorkManager` periodic work |

### 3.2 About Group

| Item | Icon | Action |
|------|------|--------|
| About Lexicon | `info` | Navigate to static About screen or dialog |
| Privacy Policy | `shield` | Open URL in browser or WebView |
| Send Feedback | `chat_bubble_outline` | Open email intent (`mailto:`) |
| Rate the App | `favorite_border` | Open Google Play listing |

---

## 4. DataStore Setup

### 4.1 Add Dependency

```kotlin
// libs.versions.toml
[versions]
datastore = "1.1.1"

[libraries]
androidx-datastore-preferences = { module = "androidx.datastore:datastore-preferences", version.ref = "datastore" }

// build.gradle.kts (app)
implementation(libs.androidx.datastore.preferences)
```

### 4.2 DataStore Instance

```kotlin
// In a dedicated file — UserPreferencesDataStore.kt
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)
```

### 4.3 Keys

```kotlin
object PreferenceKeys {
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val APP_LANGUAGE      = stringPreferencesKey("app_language")
    val DAILY_WORD_ENABLED = booleanPreferencesKey("daily_word_enabled")
}
```

### 4.4 Repository

```kotlin
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val isDarkModeEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.DARK_MODE_ENABLED] ?: false }

    val isDailyWordEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.DAILY_WORD_ENABLED] ?: true }

    val appLanguage: Flow<String> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.APP_LANGUAGE] ?: "en" }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.DARK_MODE_ENABLED] = enabled }
    }

    suspend fun setDailyWord(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.DAILY_WORD_ENABLED] = enabled }
    }

    suspend fun setLanguage(code: String) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.APP_LANGUAGE] = code }
    }
}
```

---

## 5. ViewModel — `SettingsViewModel`

```kotlin
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val isDarkModeEnabled: StateFlow<Boolean> = userPreferencesRepository.isDarkModeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    val isDailyWordEnabled: StateFlow<Boolean> = userPreferencesRepository.isDailyWordEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), true)

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setDarkMode(enabled) }
    }

    fun toggleDailyWord(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setDailyWord(enabled) }
    }
}
```

---

## 6. Fragment — `SettingsFragment`

```kotlin
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dark Mode
        lifecycleScope.launch {
            viewModel.isDarkModeEnabled.collectLatest { enabled ->
                binding.switchDarkMode.isChecked = enabled
            }
        }
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDarkMode(isChecked)
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                       else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // Daily Word
        lifecycleScope.launch {
            viewModel.isDailyWordEnabled.collectLatest { enabled ->
                binding.switchDailyWord.isChecked = enabled
            }
        }
        binding.switchDailyWord.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDailyWord(isChecked)
            if (isChecked) scheduleDailyWordNotification() else cancelDailyWordNotification()
        }

        // About items
        binding.rowAbout.setOnClickListener { /* show About dialog */ }
        binding.rowPrivacy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://yourprivacypolicy.url")))
        }
        binding.rowFeedback.setOnClickListener {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:feedback@lexicon.app")))
        }
        binding.rowRate.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireContext().packageName}")))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun scheduleDailyWordNotification() { /* WorkManager task */ }
    private fun cancelDailyWordNotification() { /* WorkManager cancel */ }
}
```

---

## 7. Dark Mode Implementation

```kotlin
// Apply theme on app launch — in MainActivity.onCreate()
lifecycleScope.launch {
    preferencesRepository.isDarkModeEnabled.first().let { isDark ->
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
```

Night theme resources already exist:
- `res/values/themes.xml` — light theme
- `res/values-night/themes.xml` — dark theme *(needs full color definitions)*

---

## 8. Daily Word — WorkManager

```kotlin
// Create DailyWordWorker.kt
class DailyWordWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // 1. Fetch word of the day from API
        // 2. Show notification via NotificationManager
        return Result.success()
    }
}

// Schedule in SettingsFragment
fun scheduleDailyWordNotification() {
    val request = PeriodicWorkRequestBuilder<DailyWordWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(calculateDelayToMorning(), TimeUnit.MILLISECONDS)
        .build()
    WorkManager.getInstance(requireContext())
        .enqueueUniquePeriodicWork("daily_word", ExistingPeriodicWorkPolicy.KEEP, request)
}
```

---

## 9. Hilt — DataStore Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userPreferencesDataStore
    }
}
```

---

## 10. Implementation Tasks

| # | Task | Priority | Status |
|---|------|----------|--------|
| 1 | Add DataStore dependency | 🔴 High | ⬜ Pending |
| 2 | Create `UserPreferencesDataStore.kt` | 🔴 High | ⬜ Pending |
| 3 | Create `UserPreferencesRepository` | 🔴 High | ⬜ Pending |
| 4 | Create `DataStoreModule` Hilt module | 🔴 High | ⬜ Pending |
| 5 | Create `SettingsViewModel` | 🔴 High | ⬜ Pending |
| 6 | Design `fragment_settings.xml` layout | 🔴 High | ⬜ Pending |
| 7 | Create `SettingsFragment` | 🔴 High | ⬜ Pending |
| 8 | Wire Dark Mode toggle → `AppCompatDelegate` | 🔴 High | ⬜ Pending |
| 9 | Define dark mode colors in `values-night/themes.xml` | 🔴 High | ⬜ Pending |
| 10 | Wire Daily Word toggle → preferences | 🟡 Medium | ⬜ Pending |
| 11 | Add Settings tab to Bottom Navigation | 🟡 Medium | ⬜ Pending |
| 12 | Wire Privacy Policy → browser intent | 🟡 Medium | ⬜ Pending |
| 13 | Wire Send Feedback → mailto intent | 🟡 Medium | ⬜ Pending |
| 14 | Wire Rate the App → Play Store intent | 🟡 Medium | ⬜ Pending |
| 15 | Add WorkManager for daily word notification | 🟢 Low | ⬜ Pending |
| 16 | Apply saved dark mode preference on app launch | 🟢 Low | ⬜ Pending |
| 17 | Add Language selector dialog | 🟢 Low | ⬜ Pending |

---

## 11. Acceptance Criteria

- [ ] Settings screen loads without crashes.
- [ ] Dark Mode toggle switches between light and dark theme immediately.
- [ ] Dark Mode preference is restored after app restart.
- [ ] Daily Word toggle state is persisted across app sessions.
- [ ] Privacy Policy link opens a browser.
- [ ] Send Feedback opens an email client.
- [ ] Rate the App opens a store listing intent.
- [ ] App version is displayed at the bottom (`BuildConfig.VERSION_NAME`).
- [ ] Settings tab is highlighted when active in bottom navigation.

---

*Related tasks: [Task Home Screen](TASK_HOME_SCREEN.md) · [Task Saved Words](TASK_SAVED_WORDS.md)*

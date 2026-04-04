# 🏛️ Architecture Guidelines — Lexicon Dictionary App

> This document defines the architectural principles, layer responsibilities, data flow patterns, and dependency injection strategy for **Lexicon**. All new features must follow these guidelines.

---

## 1. Architecture Overview

Lexicon follows **MVVM + Clean Architecture** — a layered approach popularised by Google's recommended Android architecture guidelines.

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  (Activities · Fragments · Adapters · Custom Views)     │
│                   [Observes StateFlow]                   │
├─────────────────────────────────────────────────────────┤
│                  ViewModel Layer                         │
│            (HiltViewModel · viewModelScope)              │
│                  [Calls Use-Cases]                       │
├─────────────────────────────────────────────────────────┤
│                   Domain Layer  ← PURE KOTLIN            │
│     (Use-Cases · Repository Interface · Domain Models)   │
│                  [No Android deps]                       │
├─────────────────────────────────────────────────────────┤
│                    Data Layer                            │
│  (RepositoryImpl · Remote API Services · DTOs · Mappers) │
│                [Implements Repository Interface]          │
└─────────────────────────────────────────────────────────┘
              ↑ Hilt DI wires all of this ↑
```

### Key Principles

1. **Dependency inversion** — outer layers depend on inner layers, never the reverse.
2. **Single responsibility** — each class has one reason to change.
3. **Testability** — domain layer has zero Android dependencies; fully unit-testable.
4. **Unidirectional data flow (UDF)** — UI → events → ViewModel → state → UI.

---

## 2. Package Structure

```
labs.creative.dictornarymvvm/
│
├── MainApplication.kt              # @HiltAndroidApp entry point
│
├── core/                           # Shared utilities (not domain-specific)
│   ├── BaseActivity.kt             # Network connectivity helper
│   ├── BaseFragment.kt             # Base fragment (extend as needed)
│   └── utils/
│       ├── Resource.kt             # Sealed class: Success / Error / Loading
│       └── NetworkUtils.kt         # ConnectivityManager wrapper
│
├── data/                           # DATA LAYER
│   └── remote/
│       ├── api/
│       │   ├── DatamuseApiService.kt     # Retrofit SAM — word suggestions
│       │   └── DictionaryApiService.kt   # Retrofit SAM — word details
│       └── model/                        # DTOs (JSON ↔ Kotlin)
│           ├── WordInfoDto.kt            # Full word info from Dictionary API
│           └── WordSuggestionDto.kt      # Word suggestion from Datamuse API
│
├── di/                             # DEPENDENCY INJECTION
│   ├── NetworkModule.kt            # Retrofit, OkHttp, Use-Cases
│   └── RepositoryModule.kt         # Repository binding (Impl ↔ Interface)
│
├── domain/                         # DOMAIN LAYER (pure Kotlin)
│   ├── mappers/
│   │   └── ToDomain.kt             # DTO → Domain model extension fns
│   ├── model/
│   │   ├── WordInfo.kt             # Domain model for word details
│   │   └── WordSuggestion.kt       # Domain model for suggestions
│   ├── repository/
│   │   ├── WordRepository.kt       # Interface (contract)
│   │   └── WordRepositoryImpl.kt   # Concrete implementation
│   └── usecase/
│       ├── GetWordInfoUseCase.kt       # Fetches full word detail
│       └── GetWordSuggestionsUseCase.kt # Fetches word suggestions
│
└── ui/                             # UI / PRESENTATION LAYER
    ├── MainActivity.kt             # Single-Activity host
    ├── adapter/
    │   ├── WordSearchAdapter.kt    # RecyclerView for search suggestions
    │   └── WordsTilesAdapter.kt    # RecyclerView for trending / explore tiles
    ├── fragments/
    │   ├── MainFragment.kt         # Home/Discover Words screen
    │   ├── SearchFragment.kt       # Real-time search screen
    │   └── ResultFragment.kt       # Word detail screen
    ├── viewmodel/
    │   └── SearchViewModel.kt      # StateFlow-based ViewModel for search
    └── widgets/
        └── TypeWriterView.kt       # Custom View — typewriter text animation
```

---

## 3. Layer Responsibilities

### 3.1 Presentation / UI Layer

**Files:** `ui/` package — Activities, Fragments, Adapters, Custom Views

**Responsibilities:**
- Render UI state emitted by ViewModel.
- Forward user events (text input, click, navigation) to ViewModel.
- Handle lifecycle-aware observation using `lifecycleScope` + `collectLatest`.
- **Never** perform business logic or make network calls directly.
- Use **ViewBinding** — no `findViewById`.

**Rules:**
```
✅ Observe StateFlow / LiveData
✅ Call ViewModel functions for events
✅ Use ViewBinding for all view access
✅ Null-safe binding with _binding pattern
❌ No business logic
❌ No direct repository/network calls
❌ No data transformation
```

### 3.2 ViewModel Layer

**Files:** `ui/viewmodel/` package

**Responsibilities:**
- Hold and expose UI state as `StateFlow` (not `LiveData` for new code).
- Call use-cases, handle exceptions, and update state.
- Survive configuration changes via `HiltViewModel`.
- Use `viewModelScope` for coroutines — auto-cancelled on ViewModel death.

**State pattern:**
```kotlin
// Separate StateFlows for each piece of state
private val _wordSuggestions = MutableStateFlow<List<WordSuggestion>>(emptyList())
val wordSuggestions: StateFlow<List<WordSuggestion>> get() = _wordSuggestions

private val _isLoading = MutableStateFlow<Boolean>(false)
val isLoading: StateFlow<Boolean> get() = _isLoading

private val _error = MutableStateFlow<String?>(null)
val error: StateFlow<String?> get() = _error
```

**Rules:**
```
✅ @HiltViewModel annotation
✅ Expose state as StateFlow (immutable)
✅ Use viewModelScope.launch{} for coroutines
✅ Reset _error at start of each operation
✅ Always handle exceptions with try/catch/finally
❌ No Android Context references
❌ No View references
❌ No Room/Retrofit calls — delegate to use-cases
```

### 3.3 Domain Layer

**Files:** `domain/` package — **ZERO Android framework imports**

**Responsibilities:**
- Define the **WordRepository interface** (contract).
- Contain **use-cases** — each use-case is a single, focused operation.
- Define **domain models** that are presentation-ready.
- Define **mapper extension functions** to convert DTOs → Domain models.

**Use-case pattern:**
```kotlin
class GetWordSuggestionsUseCase @Inject constructor(
    private val repository: WordRepository,
) {
    suspend operator fun invoke(meaning: String): List<WordSuggestion> {
        return repository.getWordSuggestions(meaning)
    }
}
```

**Rules:**
```
✅ Pure Kotlin classes only
✅ Use suspend operator fun invoke() pattern
✅ One use-case = one operation
✅ Inject via constructor (@Inject)
❌ No android.* imports in domain layer
❌ No Retrofit / Room references
❌ No Context
```

### 3.4 Data Layer

**Files:** `data/` + `domain/repository/WordRepositoryImpl.kt`

**Responsibilities:**
- Implement `WordRepository` interface.
- Define Retrofit API service interfaces.
- Define DTO (Data Transfer Object) data classes that match API JSON.
- Map DTOs to domain models via `ToDomain.kt` mapper functions.

**Repository implementation pattern:**
```kotlin
class WordRepositoryImpl @Inject constructor(
    private val datamuseApiService: DatamuseApiService,
    private val dictionaryApiService: DictionaryApiService,
) : WordRepository {
    override suspend fun getWordSuggestions(meaning: String): List<WordSuggestion> =
        datamuseApiService.getWordSuggestions(meaning).map { it.toDomain() }

    override suspend fun getWordInfo(word: String): List<WordInfo> =
        dictionaryApiService.getWordInfo(word).map { it.toDomain() }
}
```

**Rules:**
```
✅ DTOs annotated with @Keep (ProGuard safety)
✅ All API calls are suspend functions
✅ Map to domain models before exposing to domain layer
✅ Use @Inject constructor for DI
❌ No business logic in repository
❌ DTOs must NOT be used in domain or UI layers
```

---

## 4. Data Flow Diagram

```
User types in SearchFragment
         │
         ▼
SearchFragment.onTextChanged()
         │
         ▼
SearchViewModel.fetchWordSuggestions(query)
         │ viewModelScope.launch{}
         ▼
GetWordSuggestionsUseCase.invoke(query)
         │ suspend call
         ▼
WordRepository.getWordSuggestions(query)          [Interface]
         │
         ▼
WordRepositoryImpl.getWordSuggestions(query)       [Implementation]
         │
         ▼
DatamuseApiService.getWordSuggestions(ml=query)    [Retrofit HTTP GET]
         │ returns List<WordSuggestionDto>
         ▼
WordSuggestionDto.toDomain()                       [Mapper]
         │ returns List<WordSuggestion>
         ▼
SearchViewModel._wordSuggestions.value = result
         │ StateFlow emission
         ▼
SearchFragment.collectLatest {}
         │
         ▼
WordSearchAdapter.submitList(suggestions)          [UI Update]
```

---

## 5. Navigation Architecture

**Pattern:** Single-Activity + Navigation Component (Jetpack)

```
MainActivity
  └── NavHostFragment (nav_graph.xml)
       ├── MainFragment        (start destination)
       │     ├── → SearchFragment
       │     └── → ResultFragment
       └── SearchFragment
             └── → ResultFragment
```

**Navigation actions:**
```xml
<!-- From MainFragment -->
action_mainFragment_to_searchFragment
action_mainFragment_to_resultFragment

<!-- From SearchFragment -->
action_searchFragment_to_resultFragment
```

**ViewModel scope for navigation:**
```kotlin
// Use hiltNavGraphViewModels to share ViewModel across a navigation sub-graph
private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.nav_graph)
```

---

## 6. Dependency Injection — Hilt

### 6.1 Module Overview

| Module | Type | Provides |
|--------|------|---------|
| `NetworkModule` | `@Module @InstallIn(SingletonComponent)` | Retrofit instances, API services, Use-cases, HttpLoggingInterceptor |
| `RepositoryModule` | `@Module @InstallIn(SingletonComponent)` | `WordRepository` binding (Impl → Interface) |

### 6.2 Retrofit Instances (Named Qualifiers)

Because there are **two different base URLs**, we use `@Named` qualifiers:

```kotlin
@Named("DatamuseRetrofit")   // Base URL: https://api.datamuse.com/
@Named("DictionaryRetrofit") // Base URL: https://api.dictionaryapi.dev/api/v2/entries/en/
```

### 6.3 Dependency Graph

```
SingletonComponent
├── HttpLoggingInterceptor  (singleton)
├── Retrofit [Datamuse]     (singleton) → depends on HttpLoggingInterceptor
├── Retrofit [Dictionary]   (singleton) → depends on HttpLoggingInterceptor
├── DatamuseApiService      (singleton) → depends on Retrofit[Datamuse]
├── DictionaryApiService    (singleton) → depends on Retrofit[Dictionary]
├── WordRepositoryImpl      (singleton) → depends on both API services
├── WordRepository          (bound to WordRepositoryImpl)
├── GetWordInfoUseCase      (singleton) → depends on WordRepository
└── GetWordSuggestionsUseCase (singleton) → depends on WordRepository

ActivityComponent
└── MainActivity (@AndroidEntryPoint)

ViewModelComponent
└── SearchViewModel (@HiltViewModel)
```

### 6.4 Hilt Annotations Summary

| Annotation | Applied To | Purpose |
|-----------|-----------|---------|
| `@HiltAndroidApp` | `MainApplication` | Triggers Hilt code generation |
| `@AndroidEntryPoint` | `MainActivity` | Enables Hilt injection in Activity |
| `@HiltViewModel` | `SearchViewModel` | Marks ViewModel injectable by Hilt |
| `@Inject` | constructors | Marks constructor for injection |
| `@Module` | DI modules | Declares provider methods |
| `@InstallIn(SingletonComponent)` | DI modules | Scopes providers as app singletons |
| `@Provides` | module functions | Provides instances Hilt can't auto-create |
| `@Binds` | abstract functions | Binds interface to implementation |
| `@Singleton` | provider methods | Scopes instance to app lifetime |
| `@Named("...")` | parameters | Qualifies multiple instances of same type |

---

## 7. API Endpoints

### 7.1 Datamuse API — Word Suggestions

```
Base URL : https://api.datamuse.com/
Endpoint : GET /words?ml={meaning}
Purpose  : Returns words with similar meanings (autocomplete / suggestions)
Response : List<WordSuggestionDto> [ { "word": "...", "score": 1234, "tags": [...] } ]
```

### 7.2 Free Dictionary API — Word Details

```
Base URL : https://api.dictionaryapi.dev/api/v2/entries/en/
Endpoint : GET /{word}
Purpose  : Returns full dictionary entry — definitions, phonetics, synonyms, antonyms
Response : List<WordInfoDto> see WordInfoDto.kt for full schema
```

---

## 8. Error Handling Strategy

```
Layer         │ Responsibility
──────────────┼────────────────────────────────────────────
API Service   │ Retrofit throws HttpException / IOException
RepositoryImpl│ Propagates exceptions (no swallowing)
ViewModel     │ try/catch → sets _error StateFlow
UI Fragment   │ Observes _error → shows error message / empty state
```

**ViewModel pattern:**
```kotlin
@Suppress("TooGenericExceptionCaught")
fun fetchWordSuggestions(meaning: String) {
    viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            _wordSuggestions.value = getWordSuggestionsUseCase(meaning)
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "An unexpected error occurred"
        } finally {
            _isLoading.value = false
        }
    }
}
```

**Resource sealed class** (for richer state representation):
```kotlin
sealed class Resource<T>(val data: T? = null, val errorMessage: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(errorMessage: String) : Resource<T>(errorMessage = errorMessage)
    class Loading<T> : Resource<T>()
}
```

---

## 9. Upcoming Architecture Tasks

| Priority | Task | Notes |
|----------|------|-------|
| 🔴 High | Add local persistence (Room DB) | For saved words & search history |
| 🔴 High | Migrate ResultFragment to full ViewModel | Currently uses placeholder logic |
| 🟡 Medium | Add `SavedWordsViewModel` | For Saved Words tab |
| 🟡 Medium | Add `SettingsViewModel` + DataStore | For theme and preferences |
| 🟢 Low | Migrate `Resource<T>` into domain use-cases | Richer error handling |
| 🟢 Low | Add unit tests for all use-cases | Cover happy path & error path |
| 🟢 Low | Add integration tests for repository | Mock API responses |

---

*See also: [Design Guidelines](../design/DESIGN_GUIDELINES.md) · [Coding Guidelines](../coding/CODING_GUIDELINES.md)*

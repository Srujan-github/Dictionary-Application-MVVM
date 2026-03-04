# 💻 Coding Guidelines — Lexicon Dictionary App

> This document defines the Kotlin coding standards, naming conventions, coroutine patterns, ViewBinding rules, Detekt configuration, and best practices for **Lexicon**. All contributors must follow these guidelines.

---

## 1. General Principles

1. **Clarity over cleverness** — code is read more often than it is written.
2. **Fail fast** — surface errors early; never silently swallow exceptions.
3. **Single Responsibility** — each function, class, and module does one thing.
4. **Immutability first** — prefer `val`, `data class`, `StateFlow` over mutable state.
5. **Lean on the language** — use Kotlin idioms: extension functions, data classes, sealed classes, `when` exhaustiveness.

---

## 2. Language & Kotlin Conventions

### 2.1 `val` vs `var`

```kotlin
// ✅ Prefer val for everything that doesn't need to change
val wordSuggestions: List<WordSuggestion> = emptyList()

// ✅ Use var only when state mutation is required (e.g., MutableStateFlow value)
private val _isLoading = MutableStateFlow<Boolean>(false)

// ❌ Never use var for a value that could be val
var text = "hello" // reassigned nowhere
```

### 2.2 Data Classes

```kotlin
// ✅ Domain models are data classes
data class WordSuggestion(
    val word: String,
    val score: Int,
)

// ✅ DTOs are data classes annotated with @Keep
@Keep
data class WordSuggestionDto(
    val word: String,
    val score: Int,
    val tags: List<String>?,
)
```

### 2.3 Sealed Classes

```kotlin
// ✅ Sealed classes for finite state sets
sealed class Resource<T>(val data: T? = null, val errorMessage: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(errorMessage: String) : Resource<T>(errorMessage = errorMessage)
    class Loading<T> : Resource<T>()
}

// ✅ Always use exhaustive when expressions on sealed classes
when (resource) {
    is Resource.Success -> showContent(resource.data)
    is Resource.Error -> showError(resource.errorMessage)
    is Resource.Loading -> showLoader()
}
```

### 2.4 Extension Functions

```kotlin
// ✅ Use extension functions for mapping — keeps classes focused
fun WordSuggestionDto.toDomain(): WordSuggestion {
    return WordSuggestion(word = this.word, score = this.score)
}

// ✅ Name mapper extensions `toDomain()`, `toDto()`, `toUi()`
fun WordInfoDto.toDomain(): WordInfo { ... }
```

### 2.5 Null Safety

```kotlin
// ✅ Prefer safe call + elvis over !!
val message = error?.localizedMessage ?: "An unexpected error occurred"

// ❌ Avoid !! unless absolutely certain (document why if used)
val text = binding.tvWord.text!!.toString() // BAD

// ✅ Use _binding pattern for ViewBinding null safety
private var _binding: FragmentSearchBinding? = null
private val binding get() = _binding!!       // OK — only accessed in onViewCreated..onDestroyView

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null // ✅ Prevent memory leaks
}
```

### 2.6 String Templates

```kotlin
// ✅ String templates over concatenation
val label = "Score: ${suggestion.score}"

// ❌ Old Java style
val label = "Score: " + suggestion.score
```

### 2.7 Lambda Style

```kotlin
// ✅ Trailing lambda for last parameter
lifecycleScope.launch {
    viewModel.wordSuggestions.collectLatest { suggestions ->
        wordSearchAdapter.submitList(suggestions)
    }
}

// ✅ Single-expression functions
override fun getItemCount(): Int = wordsMatch.size

// ✅ SAM conversion for single-method interfaces (fun interface)
fun interface DatamuseApiService {
    @GET("words")
    suspend fun getWordSuggestions(@Query("ml") meaning: String): List<WordSuggestionDto>
}
```

---

## 3. Naming Conventions

### 3.1 Files & Classes

| Type | Convention | Example |
|------|-----------|---------|
| Classes / Objects | `PascalCase` | `SearchViewModel`, `NetworkModule` |
| Interfaces | `PascalCase` | `WordRepository` |
| Implementations | `PascalCase` + `Impl` suffix | `WordRepositoryImpl` |
| Use-cases | `PascalCase` + `UseCase` suffix | `GetWordInfoUseCase` |
| Adapters | `PascalCase` + `Adapter` suffix | `WordSearchAdapter` |
| Fragments | `PascalCase` + `Fragment` suffix | `SearchFragment` |
| Activities | `PascalCase` + `Activity` suffix | `MainActivity` |
| ViewModels | `PascalCase` + `ViewModel` suffix | `SearchViewModel` |
| Modules | `PascalCase` + `Module` suffix | `NetworkModule` |
| DTOs | `PascalCase` + `Dto` suffix | `WordInfoDto` |
| Custom Views | `PascalCase` + `View` suffix | `TypeWriterView` |

### 3.2 Variables & Properties

| Type | Convention | Example |
|------|-----------|---------|
| Public properties | `camelCase` | `wordSuggestions`, `isLoading` |
| Private backing fields | `_camelCase` | `_wordSuggestions`, `_isLoading` |
| Constants | `SCREAMING_SNAKE_CASE` | `ARG_PARAM1`, `DEFAULT_DELAY_MS` |
| Companion object constants | `SCREAMING_SNAKE_CASE` | `companion object { const val TAG = "..." }` |

### 3.3 Functions

| Type | Convention | Example |
|------|-----------|---------|
| Regular functions | `camelCase` | `fetchWordSuggestions()` |
| Boolean-returning | `is` / `has` / `can` prefix | `isAnimationRunning()`, `isTextInitialised()` |
| Mapper extensions | `to` prefix | `toDomain()`, `toUiModel()` |
| Lifecycle callbacks | Android standard | `onCreate()`, `onViewCreated()` |

### 3.4 XML Resources

| Type | Convention | Example |
|------|-----------|---------|
| Layout files | `snake_case` (type_name) | `fragment_search.xml`, `activity_main.xml` |
| Drawable files | `snake_case` (ic_name) | `ic_search.xml`, `ic_back_arrow.xml` |
| View IDs | `camelCase` (type_name) | `etSearch`, `rcvWordSuggestions`, `tvWord` |
| String resources | `snake_case` | `app_name`, `search_hint` |
| Color resources | `snake_case` | `color_primary`, `color_surface_variant` |
| Dimension resources | `snake_case` (dp_value) | `spacing_md`, `corner_radius_card` |

### 3.5 View ID Prefixes

| View Type | Prefix | Example |
|-----------|--------|---------|
| TextView | `tv` | `tvWord`, `tvPageEmptyHint` |
| EditText | `et` | `etSearch` |
| RecyclerView | `rcv` | `rcvWordSuggestions` |
| ImageView / ImageButton | `iv` or `ib` | `ivIcon`, `ibBack` |
| Button | `btn` | `btnSearch` |
| ProgressBar | `pb` | `pbLoading` → or `progressBar` |
| ConstraintLayout | `cl` | `clRoot` |
| LinearLayout | `ll` | `llContainer` |
| CardView | `cv` | `cvWordOfDay` |
| BottomNavigation | `bnv` | `bnvMain` |
| FragmentContainerView | `fcv` | `fcvMain` |

---

## 4. Coroutines

### 4.1 Scope Rules

| Scope | Usage |
|-------|-------|
| `viewModelScope` | All coroutines in ViewModels — auto-cancelled on ViewModel death |
| `lifecycleScope` | Fragment/Activity coroutines — auto-cancelled on lifecycle destroy |
| `GlobalScope` | ❌ **Never use** — leaks and no cancellation |

```kotlin
// ✅ ViewModel — use viewModelScope
fun fetchWordSuggestions(meaning: String) {
    viewModelScope.launch {
        ...
    }
}

// ✅ Fragment — use lifecycleScope.launch for each StateFlow
lifecycleScope.launch {
    viewModel.wordSuggestions.collectLatest { suggestions ->
        wordSearchAdapter.submitList(suggestions)
    }
}
```

### 4.2 `collectLatest` vs `collect`

```kotlin
// ✅ Use collectLatest when you only care about the latest emission
// (cancels in-progress processing when new value arrives — ideal for search UI)
viewModel.wordSuggestions.collectLatest { suggestions ->
    wordSearchAdapter.submitList(suggestions)
}

// ✅ Use collect when you must process every emission (e.g., analytics events)
viewModel.analyticsEvent.collect { event ->
    analytics.log(event)
}
```

### 4.3 Multiple Flows — Separate `launch` Blocks

```kotlin
// ✅ CORRECT — each flow in its own launch{}
// collectLatest suspends indefinitely, so chaining in one block starves later flows
lifecycleScope.launch { viewModel.wordSuggestions.collectLatest { ... } }
lifecycleScope.launch { viewModel.isLoading.collectLatest { ... } }
lifecycleScope.launch { viewModel.error.collectLatest { ... } }

// ❌ WRONG — only first collectLatest ever executes
lifecycleScope.launch {
    viewModel.wordSuggestions.collectLatest { ... }
    viewModel.isLoading.collectLatest { ... } // Never reached!
}
```

### 4.4 Dispatcher Rules

```kotlin
// Most operations run on Dispatchers.Main via viewModelScope (default)
// Move CPU-intensive or blocking operations off Main:

viewModelScope.launch {
    val result = withContext(Dispatchers.IO) {
        // Heavy computation, file I/O, blocking calls
    }
    // Back on Main — update UI state
    _wordSuggestions.value = result
}

// Retrofit suspend functions run on their own thread pool automatically
// No need to wrap Retrofit calls in withContext(Dispatchers.IO)
```

---

## 5. ViewBinding

### 5.1 Fragment Binding Pattern

```kotlin
class SearchFragment : Fragment() {

    // Step 1: Nullable backing field
    private var _binding: FragmentSearchBinding? = null

    // Step 2: Non-null accessor (only safe between onCreateView and onDestroyView)
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   // ✅ Prevent memory leak — Fragment outlives its View
    }
}
```

### 5.2 Activity Binding Pattern

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding   // No null-safe needed for Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
```

### 5.3 RecyclerView ViewHolder Binding

```kotlin
class ViewHolder(val binding: WordCardViewBinding) :
    RecyclerView.ViewHolder(binding.root)

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = WordCardViewBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
    return ViewHolder(binding)
}
```

---

## 6. Dependency Injection (Hilt) Rules

```kotlin
// ✅ Always inject via constructor (NOT field injection where avoidable)
class GetWordSuggestionsUseCase @Inject constructor(
    private val repository: WordRepository,
)

// ✅ Use @HiltViewModel for ViewModels
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getWordSuggestionsUseCase: GetWordSuggestionsUseCase,
) : ViewModel()

// ✅ Use @Singleton for app-wide single instances
@Singleton
@Provides
fun provideHttpLogger(): HttpLoggingInterceptor { ... }

// ✅ Use @Binds for interface-to-implementation binding (no function body needed)
@Binds
abstract fun bindWordRepository(impl: WordRepositoryImpl): WordRepository

// ✅ Use @Named when multiple Retrofit instances share the same type
@Named("DatamuseRetrofit")
fun provideDatamuseRetrofit(...): Retrofit

// ❌ Never use field injection (@Inject var) in non-Android classes
```

---

## 7. Error Handling

### 7.1 ViewModel Exception Pattern

```kotlin
// ✅ Standard pattern for all ViewModel operations
@Suppress("TooGenericExceptionCaught")
fun doSomething() {
    viewModelScope.launch {
        _isLoading.value = true
        _error.value = null         // Clear previous errors
        try {
            _result.value = useCase()
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "An unexpected error occurred"
        } finally {
            _isLoading.value = false // Always stop loading
        }
    }
}
```

### 7.2 Error Display in UI

```kotlin
lifecycleScope.launch {
    viewModel.error.collectLatest { error ->
        if (error != null) {
            binding.rcvWordSuggestions.visibility = View.GONE
            binding.tvPageEmptyHint.visibility = View.VISIBLE
            binding.tvPageEmptyHint.text = error
            // ✅ Consider: Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
        }
    }
}
```

### 7.3 Suppress Annotations

```kotlin
// ✅ When using broad catch (required for ViewModel error handling), document it:
@Suppress("TooGenericExceptionCaught")
// This catches all exceptions from network/parsing layers and surfaces to UI

// ✅ When Detekt flags magic numbers:
@Suppress("MagicNumber")
private var mDelay: Long = 40
```

---

## 8. Code Style & Formatting

### 8.1 Indentation

- **4 spaces** (no tabs).
- Max line length: **120 characters**.

### 8.2 Blank Lines

```kotlin
// ✅ One blank line between class members
class Foo {

    val property = ""

    fun function() { ... }

    fun anotherFunction() { ... }
}

// ✅ One blank line between top-level declarations
fun extensionOne() { ... }

fun extensionTwo() { ... }
```

### 8.3 Trailing Commas

```kotlin
// ✅ Use trailing commas in multi-line argument lists (easier diffs)
data class WordInfo(
    val word: String,
    val phonetics: List<String>,
    val meanings: List<String>,   // ← trailing comma
)
```

### 8.4 Import Ordering

1. Android imports
2. Third-party imports (Retrofit, Hilt, etc.)
3. Project imports (`labs.creative.*`)

---

## 9. Detekt Static Analysis

The project uses **Detekt** for static analysis with auto-correct enabled.

### 9.1 Configuration

```kotlin
// build.gradle.kts (app level)
detekt {
    autoCorrect = true
}
```

### 9.2 Key Rules Enforced

| Rule | Description |
|------|-------------|
| `TooGenericExceptionCaught` | Warns against catching broad `Exception` — use `@Suppress` with comment |
| `TooManyFunctions` | Classes with >11 functions should be split |
| `MagicNumber` | Numeric literals should be named constants |
| `TrailingWhitespace` | No trailing spaces |
| `UnusedImports` | Remove all unused imports |
| `MaxLineLength` | Lines max 120 characters |

### 9.3 Running Detekt

```bash
./gradlew detekt
# Output saved to: detekt_output.txt
```

### 9.4 How to Handle Detekt Warnings

```kotlin
// Option 1: Fix the code (preferred)
companion object {
    private const val ANIMATION_DELAY_MS = 40L  // Named constant
}

// Option 2: Suppress with justification (last resort)
@Suppress("TooManyFunctions")  // Required due to complex animation state machine
class TypeWriterView : AppCompatTextView { ... }
```

---

## 10. RecyclerView Adapter Best Practices

### 10.1 Prefer `DiffUtil.ItemCallback` over `notifyDataSetChanged()`

```kotlin
// ❌ Current — triggers full rebind, poor performance
fun submitList(wordsMatch: List<WordSuggestion>) {
    this.wordsMatch = wordsMatch
    notifyDataSetChanged()                // Animates poorly, O(n) rebind
}

// ✅ Preferred — use ListAdapter with DiffUtil
class WordSearchAdapter : ListAdapter<WordSuggestion, WordSearchAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WordSuggestion>() {
            override fun areItemsTheSame(old: WordSuggestion, new: WordSuggestion) =
                old.word == new.word
            override fun areContentsTheSame(old: WordSuggestion, new: WordSuggestion) =
                old == new
        }
    }
}
```

### 10.2 ViewBinding in Adapters

```kotlin
// ✅ Always use ViewBinding — never inflate manually with R.layout.*
val binding = WordCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
```

---

## 11. Custom View Guidelines (TypeWriterView)

```kotlin
// ✅ Handle all three constructor variants
constructor(context: Context) : super(context)
constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

// ✅ Use Handler(Looper.getMainLooper()) for main-thread UI updates
private val mHandler = Handler(Looper.getMainLooper())

// ✅ Clean up handler callbacks when view is detached
override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    mHandler.removeCallbacks(characterAdder)  // ← Add this to prevent leaks
}

// ✅ Remove GlobalLayoutListeners after use to prevent leaks
viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
```

---

## 12. Logging

```kotlin
// ✅ Use Timber for logging (added as dependency)
import timber.log.Timber

Timber.d("Word suggestions fetched: ${suggestions.size}")
Timber.e(exception, "Failed to fetch word suggestions")

// ❌ Never use android.util.Log directly
Log.d("TAG", "message")  // BAD — not controlled by Timber

// ✅ Plant Timber in MainApplication (debug only)
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}
```

---

## 13. BuildConfig Usage

```kotlin
// ✅ Use BuildConfig for environment-specific values
Retrofit.Builder()
    .baseUrl(BuildConfig.WORD_SEARCH_URL)      // Injected at build time
    .baseUrl(BuildConfig.DICTIONARY_SEARCH_URL)

// ✅ Check build type
if (BuildConfig.DEBUG) { ... }

// ❌ Never hardcode URLs or API keys as string literals
.baseUrl("https://api.datamuse.com/")  // BAD — use BuildConfig
```

---

## 14. Testing Guidelines

### 14.1 What to Test

| Layer | Test Type | Framework |
|-------|-----------|-----------|
| Use-cases | Unit test | JUnit 4 + MockK |
| Repository | Unit test | JUnit 4 + MockK (mock API service) |
| ViewModel | Unit test | JUnit 4 + Turbine (StateFlow testing) |
| Fragments | Instrumented | Espresso + Hilt testing |

### 14.2 Naming Convention

```kotlin
// Test class: [ClassUnderTest]Test
class SearchViewModelTest

// Test function: [methodName]_[scenario]_[expectedOutcome]
@Test
fun fetchWordSuggestions_withValidQuery_emitsNonEmptyList() { ... }

@Test
fun fetchWordSuggestions_withNetworkError_emitsError() { ... }
```

### 14.3 ViewModel Test Template

```kotlin
@HiltAndroidTest
class SearchViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val getWordSuggestionsUseCase: GetWordSuggestionsUseCase = mockk()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        viewModel = SearchViewModel(getWordSuggestionsUseCase)
    }

    @Test
    fun `fetchWordSuggestions with valid query emits suggestions`() = runTest {
        val expected = listOf(WordSuggestion("ephemeral", 1000))
        coEvery { getWordSuggestionsUseCase("ephemeral") } returns expected

        viewModel.fetchWordSuggestions("ephemeral")

        assertEquals(expected, viewModel.wordSuggestions.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }
}
```

---

*See also: [Architecture Guidelines](../architecture/ARCHITECTURE_GUIDELINES.md) · [Design Guidelines](../design/DESIGN_GUIDELINES.md)*

# 🔍 Task — Search Feature

> **Screen Name:** Search  
> **Fragment:** `SearchFragment`  
> **Layout:** `fragment_search.xml`  
> **ViewModel:** `SearchViewModel`  
> **Status:** 🟢 Mostly Done — core search works; see enhancements below

---

## 1. Overview

The **Search screen** provides real-time word suggestions as the user types. It uses the **Datamuse API** to fetch semantically related words based on meaning. Each suggestion is displayed in a `RecyclerView` and can be tapped to navigate to the word's detail screen.

---

## 2. Visual Reference

```
┌──────────────────────────────────────┐
│  [ ← back ]  Search                  │  ← Toolbar (optional)
│                                      │
│  [ 🔍  ephemeral          ]          │  ← Active search EditText
│                                      │
│  ┌─────────────────────────────┐     │  ← Suggestions list
│  │ fleeting                    │     │
│  ├─────────────────────────────┤     │
│  │ transient                   │     │
│  ├─────────────────────────────┤     │
│  │ momentary                   │     │
│  ├─────────────────────────────┤     │
│  │ brief                       │     │
│  └─────────────────────────────┘     │
│                                      │
│  [or: empty state text if no results]│
│  [or: circular progress while loading]│
│                                      │
│  [Search●] [Explore] [Saved] [⚙]    │  ← Bottom Navigation
└──────────────────────────────────────┘
```

---

## 3. Components

### 3.1 Search Input (`etSearch`)

| Attribute | Value |
|-----------|-------|
| Widget | `EditText` or `TextInputEditText` inside `TextInputLayout` |
| Shape | Pill / large corner radius (28 dp) |
| Background | Surface variant `#EBEBF5` |
| Hint text | "Search for a word..." |
| Leading icon | `ic_search` |
| Input type | `textCapWords` / plain text |
| IME action | `actionSearch` |
| Text change listener | `addTextChangedListener` → calls `viewModel.fetchWordSuggestions()` |

### 3.2 Word Suggestion List (`rcvWordSuggestions`)

| Attribute | Value |
|-----------|-------|
| Widget | `RecyclerView` |
| Layout manager | `LinearLayoutManager(VERTICAL)` |
| Adapter | `WordSearchAdapter` |
| Item layout | `word_card_view.xml` |
| Visibility | `VISIBLE` when list non-empty, `GONE` otherwise |
| Animation | Fade-in on appearance |

### 3.3 Empty / Error State (`tvPageEmptyHint`)

| Attribute | Value |
|-----------|-------|
| Widget | `TextView` |
| Text | "Type to search..." (initial) or error message from ViewModel |
| Visibility | `VISIBLE` when list empty, `GONE` otherwise |
| Text style | Body Large, secondary text color, centered |

### 3.4 Progress Bar (`progressBar`)

| Attribute | Value |
|-----------|-------|
| Widget | `CircularProgressIndicator` (Material) |
| Location | Centered above the list |
| Visibility | `VISIBLE` when `_isLoading == true` |

---

## 4. Current Implementation

### `SearchViewModel.kt`

```kotlin
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getWordSuggestionsUseCase: GetWordSuggestionsUseCase,
) : ViewModel() {

    private val _wordSuggestions = MutableStateFlow<List<WordSuggestion>>(emptyList())
    val wordSuggestions: StateFlow<List<WordSuggestion>> get() = _wordSuggestions

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

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
}
```

### Key Observation Pattern in `SearchFragment.kt`

```kotlin
// ✅ Separate launch{} blocks for each flow (collectLatest blocks the coroutine)
lifecycleScope.launch {
    viewModel.wordSuggestions.collectLatest { suggestions ->
        wordSearchAdapter.submitList(suggestions)
        binding.rcvWordSuggestions.visibility =
            if (suggestions.isNotEmpty()) View.VISIBLE else View.GONE
        binding.tvPageEmptyHint.visibility =
            if (suggestions.isEmpty()) View.VISIBLE else View.GONE
    }
}
lifecycleScope.launch {
    viewModel.isLoading.collectLatest { isLoading ->
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
lifecycleScope.launch {
    viewModel.error.collectLatest { error ->
        if (error != null) {
            binding.rcvWordSuggestions.visibility = View.GONE
            binding.tvPageEmptyHint.text = error
            binding.tvPageEmptyHint.visibility = View.VISIBLE
        }
    }
}

binding.etSearch.addTextChangedListener { text ->
    if (text.toString().isNotEmpty()) {
        viewModel.fetchWordSuggestions(text.toString())
    }
}
```

---

## 5. Known Issues & Improvements

| # | Issue | Priority | Fix |
|---|-------|----------|-----|
| 1 | No debounce on text change — fires API call on every keystroke | 🔴 High | Use `Flow.debounce(300ms)` |
| 2 | `WordSearchAdapter` uses `notifyDataSetChanged()` | 🔴 High | Migrate to `ListAdapter` + `DiffUtil` |
| 3 | Suggestion items are not clickable (no navigation to ResultFragment) | 🔴 High | Add `onItemClickListener` + navigate |
| 4 | Empty state text shows "null" if ViewModel never set it | 🟡 Medium | Set default text in XML or init |
| 5 | No minimum query length check (e.g., < 2 chars) | 🟡 Medium | Add `if (text.length >= 2)` guard |
| 6 | No voice search functionality (mic icon is decorative) | 🟢 Low | Integrate `SpeechRecognizer` |
| 7 | Search history not stored | 🟢 Low | Save to Room DB on word tap |

---

## 6. Debounce Implementation Plan

**Goal:** Avoid firing an API call on every single keystroke; wait until user pauses typing.

```kotlin
// In SearchViewModel — convert to StateFlow-based search with debounce
private val searchQuery = MutableStateFlow("")

init {
    viewModelScope.launch {
        searchQuery
            .debounce(300L)                   // Wait 300ms after last keystroke
            .filter { it.length >= 2 }        // Ignore very short queries
            .distinctUntilChanged()           // Skip if same query repeated
            .collectLatest { query ->
                fetchWordSuggestions(query)
            }
    }
}

// Call from Fragment:
binding.etSearch.addTextChangedListener { text ->
    viewModel.onSearchQueryChanged(text.toString())
}

// In ViewModel:
fun onSearchQueryChanged(query: String) {
    searchQuery.value = query
}
```

---

## 7. Item Click Navigation Plan

```kotlin
// In WordSearchAdapter — add click listener
class WordSearchAdapter(
    private var wordsMatch: List<WordSuggestion>,
    private val onWordClick: (String) -> Unit,   // ← callback
) : RecyclerView.Adapter<WordSearchAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentWord = wordsMatch[position]
        holder.binding.tvWord.text = currentWord.word
        holder.binding.root.setOnClickListener {
            onWordClick(currentWord.word)        // ← trigger navigation
        }
    }
}

// In SearchFragment:
wordSearchAdapter = WordSearchAdapter(emptyList()) { word ->
    val action = SearchFragmentDirections.actionSearchFragmentToResultFragment(word)
    findNavController().navigate(action)
}
```

---

## 8. API Details

```
Service  : DatamuseApiService
Endpoint : GET https://api.datamuse.com/words?ml={meaning}
Returns  : List<WordSuggestionDto>
           → .word  = suggested word string
           → .score = relevance score (higher = more relevant)
           → .tags  = POS tags, frequency info (optional)
```

---

## 9. Implementation Tasks

| # | Task | Priority | Status |
|---|------|----------|--------|
| 1 | Add search debounce (300 ms) via `MutableStateFlow` | 🔴 High | ⬜ Pending |
| 2 | Make suggestion items tappable → navigate to ResultFragment | 🔴 High | ⬜ Pending |
| 3 | Migrate `WordSearchAdapter` to `ListAdapter` + `DiffUtil` | 🔴 High | ⬜ Pending |
| 4 | Add Safe Args for word navigation | 🔴 High | ⬜ Pending |
| 5 | Add minimum query length guard (>= 2 chars) | 🟡 Medium | ⬜ Pending |
| 6 | Style `fragment_search.xml` to match design | 🟡 Medium | ⬜ Pending |
| 7 | Add `word_card_view.xml` arrow indicator for tappable items | 🟡 Medium | ⬜ Pending |
| 8 | Save searched word to history on tap | 🟢 Low | ⬜ Pending |
| 9 | Add voice search via SpeechRecognizer | 🟢 Low | ⬜ Pending |

---

## 10. Acceptance Criteria

- [ ] Typing in the search bar fetches suggestions from Datamuse API.
- [ ] Suggestions appear in a list with word name.
- [ ] API calls are debounced by at least 300 ms.
- [ ] Queries shorter than 2 characters do not trigger API calls.
- [ ] Loading indicator is shown while fetching.
- [ ] Errors are displayed in the empty-state text view.
- [ ] Tapping a suggestion navigates to the Word Detail screen.
- [ ] Empty state text shows "Type to search…" on initial load.

---

*Related tasks: [Task Home Screen](TASK_HOME_SCREEN.md) · [Task Word Detail](TASK_WORD_DETAIL.md)*

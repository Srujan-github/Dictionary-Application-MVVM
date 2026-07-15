# 📖 Task — Word Detail Screen (Explore)

> **Screen Name:** Word Detail / Explore  
> **Fragment:** `ResultFragment`  
> **Layout:** `fragment_result.xml`  
> **ViewModel:** `ResultViewModel` *(to be created)*  
> **Status:** 🟢 Done — full detail screen implemented, including audio pronunciation and save/share

---

## 1. Overview

The **Word Detail screen** is the richest screen in the app. It displays a complete dictionary entry for a selected word — pronunciation, definitions, example sentences, synonyms, and antonyms — in a clean, readable, card-based layout. This is the "Explore" tab content.

---

## 2. Visual Reference

```
┌──────────────────────────────────────┐
│  ←                        (share) 🔖 │  ← App bar with back/share/bookmark
│                                      │
│  Ephemeral                           │  ← Word title (Display Large)
│  🔊 /ih-FEM-er-uhl/  [adjective]     │  ← Pronunciation + Part-of-speech badge
│                                      │
│  DEFINITION                          │  ← Section label (ALL CAPS)
│  ┌────────────────────────────────┐   │
│  │ Lasting for a very short time. │   │  ← Definition card
│  │ Something that is fleeting,    │   │
│  │ transitory, or brief…          │   │
│  └────────────────────────────────┘   │
│                                      │
│  EXAMPLES                            │  ← Section label
│  ┌────────────────────────────────┐   │
│  │ ❝ The beauty of cherry…       │   │  ← Example card 1
│  └────────────────────────────────┘   │
│  ┌────────────────────────────────┐   │
│  │ ❝ Fame can be ephemeral…      │   │  ← Example card 2
│  └────────────────────────────────┘   │
│                                      │
│  SYNONYMS                            │  ← Section label
│ [fleeting] [transient] [momentary]   │  ← Lavender chips
│ [brief] [passing]                    │
│                                      │
│  ANTONYMS                            │  ← Section label
│ [permanent] [enduring] [lasting]     │  ← Pink chips
│ [eternal]                            │
│                                      │
│  [Search] [Explore●] [Saved] [⚙]    │  ← Bottom Navigation
└──────────────────────────────────────┘
```

---

## 3. Components

### 3.1 App Bar / Toolbar

| Attribute | Value |
|-----------|-------|
| Back arrow | `ic_back_arrow` — navigates up via `findNavController().navigateUp()` |
| Share button | `ic_share` — shares word + definition via Android `Sharecompat` |
| Bookmark button | `ic_bookmark` — saves word to local Room DB |
| Background | Transparent / matches screen bg |

### 3.2 Word Title

| Attribute | Value |
|-----------|-------|
| Widget | `TextView` |
| Text | Word argument passed via Safe Args |
| Style | Display Large — 32 sp, Bold, `#1A1A2E` |
| Top margin | 16 dp below toolbar |

### 3.3 Pronunciation Row

| Attribute | Value |
|-----------|-------|
| Sound icon | `ImageButton` with `ic_volume_up` (24 dp), circular bg `#EDE7FF` |
| Sound action | Tap → TTS / audio playback (see §7) |
| Phonetic text | `TextView`, Label Large, `#5C3FBE`, italic |
| Part-of-speech chip | `Chip` / `TextView`, pill shape, `#EDE7FF` bg, `#5C3FBE` text |

### 3.4 Definition Section

| Attribute | Value |
|-----------|-------|
| Section label | `"DEFINITION"` — all caps, `12 sp`, letter-spacing, secondary text |
| Card | `MaterialCardView`, 12 dp corners, surface bg `#FAF8FF` |
| Card padding | 16 dp |
| Text style | Body Large (16 sp), primary text |

### 3.5 Examples Section

| Attribute | Value |
|-----------|-------|
| Section label | `"EXAMPLES"` — all caps |
| Cards | One `MaterialCardView` per example sentence |
| Quote icon | `❝` character or `format_quote` icon, secondary text |
| Text style | Body Large (16 sp), secondary text, italic |
| Card spacing | 8 dp between cards |

### 3.6 Synonyms Section

| Attribute | Value |
|-----------|-------|
| Section label | `"SYNONYMS"` — all caps |
| Chips | `ChipGroup` with `wrap` flow, `Chip` per synonym |
| Chip bg | `#EDE7FF` (Lavender) |
| Chip text | `#5C3FBE` (Deep Purple) |
| Chip corner | 20 dp pill |
| Chip click | Navigate to ResultFragment with the synonym word |

### 3.7 Antonyms Section

| Attribute | Value |
|-----------|-------|
| Section label | `"ANTONYMS"` — all caps |
| Chips | `ChipGroup` with `wrap` flow, `Chip` per antonym |
| Chip bg | `#FFE9EF` (Pink) |
| Chip text | `#C2185B` (Deep Pink) |
| Chip click | Navigate to ResultFragment with the antonym word |

---

## 4. Data Flow

```
ResultFragment receives word (String) via Safe Args nav argument
           │
           ▼
ResultViewModel.fetchWordInfo(word)
           │
           ▼
GetWordInfoUseCase.invoke(word)
           │
           ▼
WordRepository.getWordInfo(word)
           │  HTTP GET /{word}
           ▼
DictionaryApiService  →  https://api.dictionaryapi.dev/api/v2/entries/en/{word}
           │  returns List<WordInfoDto>
           ▼
WordInfoDto.toDomain()  →  WordInfo(word, phonetics, meanings)
           │
           ▼
ResultViewModel._wordInfo.value = wordInfo
           │  StateFlow
           ▼
ResultFragment.collectLatest{}
           │
           ▼
Bind word, phonetic, meanings, synonyms, antonyms to Views
```

---

## 5. ViewModel — `ResultViewModel` *(to be created)*

```kotlin
@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getWordInfoUseCase: GetWordInfoUseCase,
) : ViewModel() {

    private val _wordInfo = MutableStateFlow<WordInfo?>(null)
    val wordInfo: StateFlow<WordInfo?> get() = _wordInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    @Suppress("TooGenericExceptionCaught")
    fun fetchWordInfo(word: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = getWordInfoUseCase(word)
                _wordInfo.value = results.firstOrNull()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Could not load word information"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

---

## 6. Domain Model Expansion Needed

The current `WordInfo` domain model is too simplified:

```kotlin
// Current — insufficient for detail screen
data class WordInfo(
    val word: String,
    val phonetics: List<String>,
    val meanings: List<String>,
)
```

**Expand to:**

```kotlin
data class WordInfo(
    val word: String,
    val phonetic: String,           // Primary phonetic e.g., "/ih-FEM-er-uhl/"
    val audioUrl: String?,          // URL for pronunciation audio
    val partOfSpeech: String,       // e.g., "adjective"
    val definitions: List<String>,  // All definitions
    val examples: List<String>,     // Example sentences
    val synonyms: List<String>,     // Synonyms
    val antonyms: List<String>,     // Antonyms
)
```

**Update `ToDomain.kt` mapper:**

```kotlin
fun WordInfoDto.toDomain(): WordInfo {
    val firstMeaning = meanings.firstOrNull()
    val firstPhonetic = phonetics.firstOrNull { it.text.isNotBlank() }
    return WordInfo(
        word = word,
        phonetic = firstPhonetic?.text ?: "",
        audioUrl = firstPhonetic?.audio,
        partOfSpeech = firstMeaning?.partOfSpeech ?: "",
        definitions = firstMeaning?.definitions?.map { it.definition } ?: emptyList(),
        examples = firstMeaning?.definitions?.mapNotNull { it.example } ?: emptyList(),
        synonyms = firstMeaning?.synonyms ?: emptyList(),
        antonyms = firstMeaning?.antonyms ?: emptyList(),
    )
}
```

---

## 7. Text-to-Speech (Pronunciation)

```kotlin
// In ResultFragment
private var tts: TextToSpeech? = null

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    tts = TextToSpeech(requireContext()) { status ->
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }
    binding.ibPronunciation.setOnClickListener {
        tts?.speak(viewModel.wordInfo.value?.word, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}

override fun onDestroyView() {
    tts?.shutdown()
    tts = null
    super.onDestroyView()
    _binding = null
}
```

---

## 8. Share & Bookmark Actions

### 8.1 Share

```kotlin
binding.ibShare.setOnClickListener {
    val word = viewModel.wordInfo.value ?: return@setOnClickListener
    val shareText = "${word.word}\n${word.phonetic}\n\n${word.definitions.firstOrNull()}"
    val intent = Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        },
        "Share via"
    )
    startActivity(intent)
}
```

### 8.2 Bookmark (Save Word)

```kotlin
// Requires Room DB — see Task: Saved Words
binding.ibBookmark.setOnClickListener {
    viewModel.toggleSavedWord()
}
```

---

## 9. Navigation Arguments (Safe Args)

Add to `nav_graph.xml`:

```xml
<fragment android:id="@+id/resultFragment" ...>
    <argument
        android:name="word"
        app:argType="string" />
</fragment>
```

In sending fragment:

```kotlin
val action = SearchFragmentDirections.actionSearchFragmentToResultFragment(word = "ephemeral")
findNavController().navigate(action)
```

In `ResultFragment`:

```kotlin
private val args: ResultFragmentArgs by navArgs()

override fun onViewCreated(...) {
    viewModel.fetchWordInfo(args.word)
}
```

---

## 10. Implementation Tasks

| # | Task | Priority | Status |
|---|------|----------|--------|
| 1 | Expand `WordInfo` domain model with full fields | 🔴 High | ✅ Done |
| 2 | Update `ToDomain.kt` mapper for full data | 🔴 High | ✅ Done |
| 3 | Create `ResultViewModel` | 🔴 High | ✅ Done |
| 4 | Add Safe Args — `word: String` argument to nav_graph | 🔴 High | ✅ Done |
| 5 | Design `fragment_result.xml` layout | 🔴 High | ✅ Done |
| 6 | Wire `ResultFragment` to `ResultViewModel` | 🔴 High | ✅ Done |
| 7 | Bind definition, examples to cards | 🔴 High | ✅ Done |
| 8 | Implement synonym chips (lavender) with navigation | 🔴 High | ✅ Done |
| 9 | Implement antonym chips (pink) with navigation | 🔴 High | ✅ Done |
| 10 | Add TTS pronunciation on mic button tap | 🟡 Medium | ✅ Done |
| 11 | Implement share action | 🟡 Medium | ✅ Done |
| 12 | Implement bookmark (save to Room DB) | 🟡 Medium | ✅ Done |
| 13 | Handle word not found (404) gracefully | 🟡 Medium | ✅ Done |
| 14 | Audio pronunciation via audio URL | 🟢 Low | ✅ Done (falls back to TTS if playback fails) |

---

## 11. Acceptance Criteria

- [x] Screen receives word as navigation argument and loads it.
- [x] Word title, phonetic, and part-of-speech are displayed.
- [x] At least one definition is shown in a card.
- [x] Example sentences are shown (if available).
- [x] Synonym chips are shown in lavender.
- [x] Antonym chips are shown in pink.
- [x] Tapping a synonym/antonym chip navigates to that word's detail.
- [x] Pronunciation button fires TTS.
- [x] Loading state shown while API fetches data.
- [x] Error state shown if word not found.
- [x] Back button returns to previous screen.

---

*Related tasks: [Task Search](TASK_SEARCH.md) · [Task Saved Words](TASK_SAVED_WORDS.md)*

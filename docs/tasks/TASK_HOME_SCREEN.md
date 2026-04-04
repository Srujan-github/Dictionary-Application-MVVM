# 🏠 Task — Home Screen (Discover Words)

> **Screen Name:** Discover Words  
> **Fragment:** `MainFragment`  
> **Layout:** `fragment_main.xml`  
> **Status:** 🟡 In Progress — basic scaffold exists; sections below are pending

---

## 1. Overview

The **Home Screen** is the default start destination of the app. It gives users an instant, delightful way to discover vocabulary through three sections:

1. **Recent** — chips of recently searched words.
2. **Word of the Day** — highlighted card with the day's featured word.
3. **Trending** — ranked list of popular/trending words.

The bottom navigation bar is always visible with four tabs: Search · Explore · Saved · Settings.

---

## 2. Visual Reference

```
┌──────────────────────────────────────┐
│  Discover Words                      │  ← Display headline
│  Explore meanings, synonyms…         │  ← Subtitle / TypeWriter tagline
│                                      │
│  [ 🔍  Search for a word...  🎤 ]    │  ← Tappable search bar (→ SearchFragment)
│                                      │
│  ⏱ Recent                            │  ← Section header
│  [Paradigm] [Aesthetic] [Catalyst]   │  ← Horizontal chip row
│  [Nuance] [Pragmatic]               │
│                                      │
│  ✨ Word of the Day                  │  ← Section header with sparkle icon
│ ┌────────────────────────────────┐   │
│ │ Ephemeral               [→]   │   │  ← Purple card (tap → ResultFragment)
│ │ /ih-FEM-er-uhl/               │   │
│ │ Lasting for a very short…     │   │
│ └────────────────────────────────┘   │
│                                      │
│  📈 Trending                         │  ← Section header
│  ┌──────────────────────────────┐    │
│  │ 01  Serendipity  The occ…  → │    │
│  │ 02  Ubiquitous   Present…  → │    │
│  │ 03  Eloquent     Fluent…   → │    │
│  └──────────────────────────────┘    │
│                                      │
│  [Search] [Explore] [Saved] [⚙]      │  ← Bottom Navigation Bar
└──────────────────────────────────────┘
```

---

## 3. Sections & Components

### 3.1 Screen Header

| Attribute | Value |
|-----------|-------|
| Title text | "Discover Words" |
| Title style | Headline Large (28 sp, Bold) |
| Subtitle | "Explore meanings, synonyms, and more" |
| Subtitle style | Body Medium (14 sp, secondary text color) |
| Top margin | 32 dp |

**Stretch goal:** Replace static subtitle with `TypeWriterView` cycling through taglines:
- "Explore meanings, synonyms, and more"
- "Discover the beauty of language"
- "Your personal vocabulary companion"

### 3.2 Search Bar

| Attribute | Value |
|-----------|-------|
| Shape | Pill (28 dp corner radius) |
| Background | `#EBEBF5` (Surface Variant) |
| Height | 56 dp |
| Leading icon | `ic_search` (24 dp, hint color) |
| Trailing icon | `ic_mic` (24 dp, hint color) — voice search placeholder |
| Placeholder text | "Search for a word..." |
| Click action | Navigate to `SearchFragment` |
| Top margin from header | 24 dp |

> ⚠️ The search bar on the Home screen is a **navigation trigger** (not editable). Real typing happens in `SearchFragment`.

### 3.3 Recent Section

| Attribute | Value |
|-----------|-------|
| Label | "Recent" with clock icon |
| Label style | Title Large (18 sp, SemiBold) |
| Chip list | Horizontal scrollable `HorizontalScrollView` + `LinearLayout` OR `RecyclerView` (horizontal) |
| Chip shape | Pill — 20 dp corners |
| Chip background | White with 1 dp border (`#C8C4D8`) |
| Chip text style | Body Medium (14 sp) primary text |
| Chip click action | Navigate to ResultFragment with the word |
| Data source | Local storage (Room DB / SharedPreferences) — see Task: Local Persistence |

### 3.4 Word of the Day Card

| Attribute | Value |
|-----------|-------|
| Card background | Pale purple gradient `#EDE7FF` → `#DDD5F8` |
| Card corner radius | 20 dp |
| Card padding | 20 dp |
| Word text | Headline Medium (22 sp), Bold, `#5C3FBE` |
| Phonetic text | Label Large (14 sp), italic, `#5C3FBE` |
| Definition text | Body Medium (14 sp), `#5C3FBE` |
| Arrow FAB | 48×48 dp, deep purple bg, white arrow icon, 12 dp corners |
| Click action | Navigate to ResultFragment with the word |
| Data source | Local hardcoded for MVP; later from a curated API or Room DB |

### 3.5 Trending Section

| Attribute | Value |
|-----------|-------|
| Label | "Trending" with trending_up icon |
| Label style | Title Large (18 sp, SemiBold) |
| List type | `RecyclerView` with `LinearLayoutManager` (vertical) |
| Adapter | `WordsTilesAdapter` |
| Item layout | `word_card_view.xml` |
| Rank badge | Circular, 36×36 dp, pale purple bg, "01"/"02"/"03" text |
| Word text | Body Large (16 sp), Bold |
| Description | Body Medium (14 sp), secondary text, 1-line truncation |
| Trailing arrow | `arrow_forward_ios` icon, hint color |
| Item spacing | 8 dp between items |
| Data source | Hardcoded for MVP; later from Datamuse popular words API |

---

## 4. Navigation

| Action | Destination |
|--------|-------------|
| Tap search bar | `action_mainFragment_to_searchFragment` |
| Tap "Word of the Day" arrow | `action_mainFragment_to_resultFragment` (pass word as arg) |
| Tap any recent chip | `action_mainFragment_to_resultFragment` (pass word as arg) |
| Tap any trending item | `action_mainFragment_to_resultFragment` (pass word as arg) |

---

## 5. ViewModel — `MainViewModel` (to be created)

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWordInfoUseCase: GetWordInfoUseCase,
) : ViewModel() {

    private val _wordOfTheDay = MutableStateFlow<WordInfo?>(null)
    val wordOfTheDay: StateFlow<WordInfo?> get() = _wordOfTheDay

    private val _recentWords = MutableStateFlow<List<String>>(emptyList())
    val recentWords: StateFlow<List<String>> get() = _recentWords

    private val _trendingWords = MutableStateFlow<List<WordsTilesAdapter.WordInfo>>(emptyList())
    val trendingWords: StateFlow<List<WordsTilesAdapter.WordInfo>> get() = _trendingWords

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    // MVP: Load hardcoded data; production: call API
    fun loadHomeData() {
        _trendingWords.value = listOf(
            WordsTilesAdapter.WordInfo("Serendipity", "The occurrence of events by chance in a..."),
            WordsTilesAdapter.WordInfo("Ubiquitous", "Present, appearing, or found everywhere."),
            WordsTilesAdapter.WordInfo("Eloquent", "Fluent or persuasive in speaking or writin..."),
        )
        _wordOfTheDay.value = WordInfo(
            word = "Ephemeral",
            phonetics = listOf("/ih-FEM-er-uhl/"),
            meanings = listOf("Lasting for a very short time. Something that is fleeting, transitory, or brief in duration."),
        )
    }
}
```

---

## 6. Implementation Tasks

| # | Task | Priority | Status |
|---|------|----------|--------|
| 1 | Design `fragment_main.xml` layout matching wireframe | 🔴 High | ⬜ Pending |
| 2 | Create `MainViewModel` with hardcoded MVP data | 🔴 High | ⬜ Pending |
| 3 | Wire `MainFragment` to `MainViewModel` | 🔴 High | ⬜ Pending |
| 4 | Implement Recent Chips (horizontal scroll) | 🔴 High | ⬜ Pending |
| 5 | Implement Word of the Day card | 🔴 High | ⬜ Pending |
| 6 | Implement Trending RecyclerView with `WordsTilesAdapter` | 🔴 High | ⬜ Pending |
| 7 | Wire search bar tap → navigate to SearchFragment | 🔴 High | ⬜ Pending |
| 8 | Wire all item taps → navigate to ResultFragment with word arg | 🟡 Medium | ⬜ Pending |
| 9 | Add `TypeWriterView` tagline below header | 🟡 Medium | ⬜ Pending |
| 10 | Persist recent words to SharedPreferences / Room | 🟢 Low | ⬜ Pending |
| 11 | Connect Word of the Day to real API | 🟢 Low | ⬜ Pending |

---

## 7. Acceptance Criteria

- [ ] Home screen loads without crashes on API 24+.
- [ ] Search bar tap navigates to SearchFragment.
- [ ] Word of the Day card displays a word, phonetic, and short definition.
- [ ] Trending list shows at least 3 items with rank badges.
- [ ] Tapping any word navigates to ResultFragment.
- [ ] Recent chips section is visible (at least with placeholder data).
- [ ] Bottom navigation bar is functional.
- [ ] Screen matches the design reference (colors, spacing, typography).

---

*Related tasks: [Task Search](TASK_SEARCH.md) · [Task Word Detail](TASK_WORD_DETAIL.md)*

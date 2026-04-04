# 🔖 Task — Saved Words Screen

> **Screen Name:** Saved Words  
> **Fragment:** `SavedWordsFragment` *(to be created)*  
> **Layout:** `fragment_saved.xml` *(to be created)*  
> **ViewModel:** `SavedWordsViewModel` *(to be created)*  
> **Status:** 🔴 Not Started — requires Room DB setup first

---

## 1. Overview

The **Saved Words** screen lets users maintain a personal vocabulary list. Words saved on the Word Detail screen (via the bookmark icon) are persisted to a local **Room database** and displayed here as a scrollable list. Each item can be tapped to view the word's detail again.

---

## 2. Visual Reference

```
┌──────────────────────────────────────┐
│  Saved Words                         │  ← Screen title (Headline Large)
│  1 word saved                        │  ← Dynamic subtitle
│                                      │
│  ╔════════════════════════════════╗   │
│  ║  📖  Ephemeral            →  ║   │  ← Saved word card
│  ║       adjective  (purple)     ║   │
│  ║       Lasting for a very…     ║   │
│  ╚════════════════════════════════╝   │
│                                      │
│  [Empty state if no saved words]     │
│  "No saved words yet.                │
│   Search and bookmark words          │
│   to build your vocabulary!"         │
│                                      │
│  [Search] [Explore] [Saved●] [⚙]   │  ← Bottom Navigation
└──────────────────────────────────────┘
```

---

## 3. Features

### 3.1 Saved Word List

| Attribute | Value |
|-----------|-------|
| Widget | `RecyclerView` with `LinearLayoutManager` (vertical) |
| Item layout | `item_saved_word.xml` (new layout to create) |
| Swipe to delete | `ItemTouchHelper` with swipe-left action |
| Click | Navigate to ResultFragment with the word |

### 3.2 Saved Word Card

| Attribute | Value |
|-----------|-------|
| Card corner | 16 dp |
| Card bg | Surface `#FAF8FF` |
| Card padding | 16 dp |
| Icon | Book icon (`menu_book`), 40×40 dp, circular `#EDE7FF` bg |
| Word text | Body Large, Bold, primary text |
| Part-of-speech | Label Medium, `#5C3FBE` (accent purple) |
| Short definition | Body Medium, secondary text, truncated to 2 lines |
| Trailing arrow | `arrow_forward_ios` icon |

### 3.3 Empty State

| Attribute | Value |
|-----------|-------|
| Illustration | Centered icon (book or bookmark, large, 80 dp) |
| Primary text | "No saved words yet." — Body Large, Bold |
| Secondary text | "Search and bookmark words to build your vocabulary!" — Body Medium, secondary |
| CTA button *(optional)* | "Explore Words" → navigates to Search tab |

### 3.4 Subtitle Counter

```
"1 word saved" / "5 words saved" / "No words saved"
```

Updates reactively via StateFlow from ViewModel.

---

## 4. Room Database Setup

### 4.1 Entity

```kotlin
@Entity(tableName = "saved_words")
data class SavedWordEntity(
    @PrimaryKey val word: String,           // Unique identifier
    val phonetic: String,
    val partOfSpeech: String,
    val shortDefinition: String,
    val savedAt: Long = System.currentTimeMillis(),
)
```

### 4.2 DAO

```kotlin
@Dao
interface SavedWordDao {

    @Query("SELECT * FROM saved_words ORDER BY savedAt DESC")
    fun getAllSavedWords(): Flow<List<SavedWordEntity>>   // Reactive — emits on change

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWord(word: SavedWordEntity)

    @Delete
    suspend fun deleteWord(word: SavedWordEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_words WHERE word = :word)")
    suspend fun isWordSaved(word: String): Boolean

    @Query("DELETE FROM saved_words WHERE word = :word")
    suspend fun deleteWordByName(word: String)
}
```

### 4.3 Database

```kotlin
@Database(entities = [SavedWordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedWordDao(): SavedWordDao

    companion object {
        const val DATABASE_NAME = "lexicon_db"
    }
}
```

### 4.4 DI Module — `DatabaseModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideSavedWordDao(database: AppDatabase): SavedWordDao {
        return database.savedWordDao()
    }
}
```

---

## 5. Repository Expansion

Add to `WordRepository` interface:

```kotlin
interface WordRepository {
    suspend fun getWordSuggestions(meaning: String): List<WordSuggestion>
    suspend fun getWordInfo(word: String): List<WordInfo>
    fun getSavedWords(): Flow<List<SavedWord>>             // NEW
    suspend fun saveWord(word: SavedWord)                  // NEW
    suspend fun deleteWord(word: String)                   // NEW
    suspend fun isWordSaved(word: String): Boolean         // NEW
}
```

Domain model for saved words:

```kotlin
data class SavedWord(
    val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val shortDefinition: String,
    val savedAt: Long,
)
```

---

## 6. Use Cases

```kotlin
class GetSavedWordsUseCase @Inject constructor(
    private val repository: WordRepository,
) {
    operator fun invoke(): Flow<List<SavedWord>> = repository.getSavedWords()
}

class SaveWordUseCase @Inject constructor(
    private val repository: WordRepository,
) {
    suspend operator fun invoke(word: SavedWord) = repository.saveWord(word)
}

class DeleteSavedWordUseCase @Inject constructor(
    private val repository: WordRepository,
) {
    suspend operator fun invoke(word: String) = repository.deleteWord(word)
}
```

---

## 7. ViewModel — `SavedWordsViewModel`

```kotlin
@HiltViewModel
class SavedWordsViewModel @Inject constructor(
    private val getSavedWordsUseCase: GetSavedWordsUseCase,
    private val deleteSavedWordUseCase: DeleteSavedWordUseCase,
) : ViewModel() {

    val savedWords: StateFlow<List<SavedWord>> = getSavedWordsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList(),
        )

    fun deleteWord(word: String) {
        viewModelScope.launch {
            deleteSavedWordUseCase(word)
        }
    }
}
```

---

## 8. Fragment — `SavedWordsFragment`

```kotlin
@AndroidEntryPoint
class SavedWordsFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SavedWordsViewModel by viewModels()
    private lateinit var adapter: SavedWordsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SavedWordsAdapter { word ->
            val action = SavedWordsFragmentDirections.actionSavedToResult(word)
            findNavController().navigate(action)
        }
        binding.rcvSavedWords.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvSavedWords.adapter = adapter

        lifecycleScope.launch {
            viewModel.savedWords.collectLatest { words ->
                adapter.submitList(words)
                binding.tvWordCount.text = when (words.size) {
                    0 -> "No words saved"
                    1 -> "1 word saved"
                    else -> "${words.size} words saved"
                }
                binding.emptyState.visibility = if (words.isEmpty()) View.VISIBLE else View.GONE
                binding.rcvSavedWords.visibility = if (words.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }

        // Swipe to delete
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(...) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val word = adapter.currentList[viewHolder.adapterPosition].word
                viewModel.deleteWord(word)
            }
        }).attachToRecyclerView(binding.rcvSavedWords)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

## 9. Navigation — Bottom Navigation Integration

The existing `MainActivity` uses a bottom navigation bar. Add the Saved tab destination:

```xml
<!-- nav_graph.xml — add new fragment -->
<fragment
    android:id="@+id/savedWordsFragment"
    android:name="labs.creative.dictornarymvvm.ui.fragments.SavedWordsFragment"
    android:label="Saved"
    tools:layout="@layout/fragment_saved">
    <action
        android:id="@+id/action_saved_to_result"
        app:destination="@id/resultFragment" />
</fragment>
```

---

## 10. Implementation Tasks

| # | Task | Priority | Status |
|---|------|----------|--------|
| 1 | Add Room dependency to `build.gradle.kts` | 🔴 High | ⬜ Pending |
| 2 | Create `SavedWordEntity` | 🔴 High | ⬜ Pending |
| 3 | Create `SavedWordDao` | 🔴 High | ⬜ Pending |
| 4 | Create `AppDatabase` | 🔴 High | ⬜ Pending |
| 5 | Create `DatabaseModule` (Hilt) | 🔴 High | ⬜ Pending |
| 6 | Add `SavedWord` domain model | 🔴 High | ⬜ Pending |
| 7 | Expand `WordRepository` interface with saved word methods | 🔴 High | ⬜ Pending |
| 8 | Implement repository methods in `WordRepositoryImpl` | 🔴 High | ⬜ Pending |
| 9 | Create `GetSavedWordsUseCase`, `SaveWordUseCase`, `DeleteSavedWordUseCase` | 🔴 High | ⬜ Pending |
| 10 | Create `SavedWordsViewModel` | 🔴 High | ⬜ Pending |
| 11 | Design `fragment_saved.xml` with list + empty state | 🔴 High | ⬜ Pending |
| 12 | Design `item_saved_word.xml` item layout | 🔴 High | ⬜ Pending |
| 13 | Create `SavedWordsAdapter` (ListAdapter + DiffUtil) | 🔴 High | ⬜ Pending |
| 14 | Create `SavedWordsFragment` and wire to ViewModel | 🔴 High | ⬜ Pending |
| 15 | Integrate bookmark button in `ResultFragment` → calls `SaveWordUseCase` | 🔴 High | ⬜ Pending |
| 16 | Add Saved tab to Bottom Navigation in `activity_main.xml` | 🟡 Medium | ⬜ Pending |
| 17 | Add swipe-to-delete with undo Snackbar | 🟡 Medium | ⬜ Pending |
| 18 | Update saved word count in subtitle reactively | 🟡 Medium | ⬜ Pending |

---

## 11. Acceptance Criteria

- [ ] Room database is created without crashing.
- [ ] Bookmarking a word in ResultFragment saves it to Room DB.
- [ ] SavedWordsFragment shows the list of saved words in reverse chronological order.
- [ ] Subtitle shows correct count (e.g., "3 words saved").
- [ ] Tapping a saved word navigates to its detail screen.
- [ ] Swiping a word left deletes it from the list.
- [ ] Empty state is shown when no words are saved.
- [ ] Bookmark toggle icon updates correctly (filled when saved, outline when not).

---

*Related tasks: [Task Word Detail](TASK_WORD_DETAIL.md) · [Task Settings](TASK_SETTINGS.md)*

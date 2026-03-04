package labs.creative.dictornarymvvm.domain.repository

import kotlinx.coroutines.flow.Flow
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.model.WordSuggestion

interface WordRepository {
    suspend fun getWordSuggestions(meaning: String): List<WordSuggestion>
    suspend fun getWordInfo(word: String): List<WordInfo>
    fun getSavedWords(): Flow<List<SavedWord>>
    suspend fun saveWord(word: SavedWord)
    suspend fun deleteWord(word: String)
    suspend fun isWordSaved(word: String): Boolean
}

package labs.creative.dictornarymvvm.domain.repository

import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.model.WordSuggestion

interface WordRepository {
    suspend fun getWordSuggestions(meaning: String): List<WordSuggestion>
    suspend fun getWordInfo(word: String): List<labs.creative.dictornarymvvm.domain.model.WordInfo>
}

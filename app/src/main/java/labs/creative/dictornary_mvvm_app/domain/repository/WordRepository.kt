package labs.creative.dictornary_mvvm_app.domain.repository

import labs.creative.dictornary_mvvm_app.domain.model.WordInfo
import labs.creative.dictornary_mvvm_app.domain.model.WordSuggestion


interface WordRepository {
    suspend fun getWordSuggestions(meaning: String): List<WordSuggestion>
    suspend fun getWordInfo(word: String): List<WordInfo>
}

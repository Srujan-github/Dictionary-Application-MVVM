package labs.creative.dictornary_mvvm_app.domain.repository


import labs.creative.dictornary_mvvm_app.data.remote.api.DatamuseApiService
import labs.creative.dictornary_mvvm_app.data.remote.api.DictionaryApiService
import labs.creative.dictornary_mvvm_app.domain.mappers.toDomain
import labs.creative.dictornary_mvvm_app.domain.model.WordInfo
import labs.creative.dictornary_mvvm_app.domain.model.WordSuggestion
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val datamuseApiService: DatamuseApiService,
    private val dictionaryApiService: DictionaryApiService
) : WordRepository {

    override suspend fun getWordSuggestions(meaning: String): List<WordSuggestion> {
        return datamuseApiService.getWordSuggestions(meaning).map { it.toDomain() }
    }

    override suspend fun getWordInfo(word: String): List<WordInfo> {
        return dictionaryApiService.getWordInfo(word).map { it.toDomain() }
    }
}

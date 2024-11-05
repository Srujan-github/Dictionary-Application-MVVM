package labs.creative.dictornarymvvm.domain.repository



import labs.creative.dictornarymvvm.data.remote.api.DatamuseApiService
import labs.creative.dictornarymvvm.data.remote.api.DictionaryApiService
import labs.creative.dictornarymvvm.domain.mappers.toDomain
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.model.WordSuggestion

import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val datamuseApiService: DatamuseApiService,
    private val dictionaryApiService: DictionaryApiService
) : labs.creative.dictornarymvvm.domain.repository.WordRepository {

    override suspend fun getWordSuggestions(meaning: String): List<WordSuggestion> {
        return datamuseApiService.getWordSuggestions(meaning).map { it.toDomain() }
    }

    override suspend fun getWordInfo(word: String): List<labs.creative.dictornarymvvm.domain.model.WordInfo> {
        return dictionaryApiService.getWordInfo(word).map { it.toDomain() }
    }
}

package labs.creative.dictornarymvvm.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import labs.creative.dictornarymvvm.data.local.dao.SavedWordDao
import labs.creative.dictornarymvvm.data.local.entity.SavedWordEntity
import labs.creative.dictornarymvvm.data.remote.api.DatamuseApiService
import labs.creative.dictornarymvvm.data.remote.api.DictionaryApiService
import labs.creative.dictornarymvvm.domain.mappers.toDomain
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.model.WordSuggestion
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val dictionaryApiService: DictionaryApiService,
    private val datamuseApiService: DatamuseApiService,
    private val savedWordDao: SavedWordDao,
) : WordRepository {

    override suspend fun getWordSuggestions(meaning: String): List<WordSuggestion> {
        return datamuseApiService.getWordSuggestions(meaning).map { it.toDomain() }
    }

    override suspend fun getWordInfo(word: String): List<WordInfo> {
        return dictionaryApiService.getWordInfo(word).map { it.toDomain() }
    }

    override fun getSavedWords(): Flow<List<SavedWord>> {
        return savedWordDao.getAllSavedWords().map { entities ->
            entities.map { entity ->
                SavedWord(
                    word = entity.word,
                    phonetic = entity.phonetic,
                    partOfSpeech = entity.partOfSpeech,
                    shortDefinition = entity.shortDefinition,
                    savedAt = entity.savedAt,
                )
            }
        }
    }

    override suspend fun saveWord(word: SavedWord) {
        savedWordDao.saveWord(
            SavedWordEntity(
                word = word.word,
                phonetic = word.phonetic,
                partOfSpeech = word.partOfSpeech,
                shortDefinition = word.shortDefinition,
                savedAt = word.savedAt,
            ),
        )
    }

    override suspend fun deleteWord(word: String) {
        savedWordDao.deleteWordByName(word)
    }

    override suspend fun isWordSaved(word: String): Boolean {
        return savedWordDao.isWordSaved(word)
    }
}

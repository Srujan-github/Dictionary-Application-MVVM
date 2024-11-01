package labs.creative.dictornary_mvvm_app.domain.usecase


import labs.creative.dictornary_mvvm_app.domain.model.WordInfo
import labs.creative.dictornary_mvvm_app.domain.repository.WordRepository
import javax.inject.Inject

class GetWordInfoUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(word: String): List<WordInfo> {
        return repository.getWordInfo(word)
    }
}

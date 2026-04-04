package labs.creative.dictornarymvvm.domain.usecase

import labs.creative.dictornarymvvm.domain.repository.WordRepository
import javax.inject.Inject

class DeleteSavedWordUseCase @Inject constructor(
    private val repository: WordRepository,
) {
    suspend operator fun invoke(word: String) = repository.deleteWord(word)
}

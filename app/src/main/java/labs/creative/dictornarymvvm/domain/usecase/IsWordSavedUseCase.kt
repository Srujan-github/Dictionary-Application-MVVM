package labs.creative.dictornarymvvm.domain.usecase

import labs.creative.dictornarymvvm.domain.repository.WordRepository
import javax.inject.Inject

class IsWordSavedUseCase @Inject constructor(
    private val repository: WordRepository,
) {
    suspend operator fun invoke(word: String): Boolean = repository.isWordSaved(word)
}

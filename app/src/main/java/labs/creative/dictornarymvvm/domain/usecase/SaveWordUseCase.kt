package labs.creative.dictornarymvvm.domain.usecase

import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.repository.WordRepository
import javax.inject.Inject

class SaveWordUseCase @Inject constructor(
    private val repository: WordRepository,
) {
    suspend operator fun invoke(word: SavedWord) = repository.saveWord(word)
}

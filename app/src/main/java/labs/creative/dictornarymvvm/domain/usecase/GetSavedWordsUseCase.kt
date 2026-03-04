package labs.creative.dictornarymvvm.domain.usecase

import kotlinx.coroutines.flow.Flow
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.repository.WordRepository
import javax.inject.Inject

class GetSavedWordsUseCase @Inject constructor(
    private val repository: WordRepository,
) {
    operator fun invoke(): Flow<List<SavedWord>> = repository.getSavedWords()
}

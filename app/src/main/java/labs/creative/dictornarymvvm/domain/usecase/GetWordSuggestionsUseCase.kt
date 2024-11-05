package labs.creative.dictornarymvvm.domain.usecase


import labs.creative.dictornary_mvvm_app.domain.model.WordSuggestion
import labs.creative.dictornarymvvm.domain.repository.WordRepository
import javax.inject.Inject

class GetWordSuggestionsUseCase @Inject constructor(
    private val repository: labs.creative.dictornarymvvm.domain.repository.WordRepository
) {
    suspend operator fun invoke(meaning: String): List<WordSuggestion> {
        return repository.getWordSuggestions(meaning)
    }
}

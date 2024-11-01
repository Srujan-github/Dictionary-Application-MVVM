package labs.creative.dictornary_mvvm_app.domain.usecase


import labs.creative.dictornary_mvvm_app.domain.model.WordSuggestion
import labs.creative.dictornary_mvvm_app.domain.repository.WordRepository
import javax.inject.Inject

class GetWordSuggestionsUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(meaning: String): List<WordSuggestion> {
        return repository.getWordSuggestions(meaning)
    }
}

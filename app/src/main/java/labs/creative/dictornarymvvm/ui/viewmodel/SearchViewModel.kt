package labs.creative.dictornarymvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.domain.model.WordSuggestion
import labs.creative.dictornarymvvm.domain.usecase.GetWordSuggestionsUseCase
import javax.inject.Inject

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val suggestions: List<WordSuggestion>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getWordSuggestionsUseCase: GetWordSuggestionsUseCase,
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_DELAY_MS = 300L
        private const val MIN_QUERY_LENGTH = 2
    }

    private val _searchQuery = MutableStateFlow("")

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> get() = _uiState

    init {
        observeSearchQuery()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(DEBOUNCE_DELAY_MS)
                .flatMapLatest { query ->
                    val trimmedQuery = query.trim()
                    if (trimmedQuery.length < MIN_QUERY_LENGTH) {
                        flow { emit(SearchUiState.Idle) }
                    } else {
                        flow {
                            emit(SearchUiState.Loading)
                            try {
                                val results = getWordSuggestionsUseCase(trimmedQuery)
                                if (results.isEmpty()) {
                                    emit(SearchUiState.Error("No search results found."))
                                } else {
                                    emit(SearchUiState.Success(results))
                                }
                            } catch (e: java.io.IOException) {
                                timber.log.Timber.e(e, "Network error during search")
                                emit(SearchUiState.Error("Network error: Please check your connection."))
                            } catch (e: retrofit2.HttpException) {
                                timber.log.Timber.e(e, "Server error: ${e.code()}")
                                emit(SearchUiState.Error("Server error: ${e.message()}"))
                            } catch (e: kotlinx.coroutines.CancellationException) {
                                // Thrown when flatMapLatest cancels this in-flight request in
                                // favor of a newer query, or when viewModelScope is cleared.
                                // Must propagate so structured concurrency can unwind properly.
                                throw e
                            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                                timber.log.Timber.e(e, "Unexpected error during search")
                                emit(SearchUiState.Error(e.localizedMessage ?: "An unexpected error occurred"))
                            }
                        }
                    }
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
}

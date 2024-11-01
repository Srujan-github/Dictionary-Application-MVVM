package labs.creative.dictornary_mvvm_app.data.remote.model

data class WordSuggestionDto(
    val word: String,
    val score: Int,
    val tags: List<String>?
)

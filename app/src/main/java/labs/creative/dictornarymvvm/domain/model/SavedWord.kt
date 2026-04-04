package labs.creative.dictornarymvvm.domain.model

data class SavedWord(
    val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val shortDefinition: String,
    val savedAt: Long,
)

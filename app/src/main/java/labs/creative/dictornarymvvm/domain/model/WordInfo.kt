package labs.creative.dictornarymvvm.domain.model

data class WordInfo(
    val word: String,
    val phonetic: String,
    val audioUrl: String?,
    val partOfSpeech: String,
    val definitions: List<String>,
    val examples: List<String>,
    val synonyms: List<String>,
    val antonyms: List<String>,
)

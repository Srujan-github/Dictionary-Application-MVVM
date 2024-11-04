package labs.creative.dictornary_mvvm_app.data.remote.model


import androidx.annotation.Keep

@Keep
data class WordInfoDto(
    val license: License,
    val meanings: List<Meaning>,
    val phonetics: List<Phonetic>,
    val sourceUrls: List<String>,
    val word: String
)

@Keep
data class Phonetic(
    val audio: String,
    val license: License,
    val sourceUrl: String,
    val text: String
)

@Keep
data class Definition(
    val antonyms: List<Any>,
    val definition: String,
    val example: String,
    val synonyms: List<Any>
)
@Keep
data class Meaning(
    val antonyms: List<String>,
    val definitions: List<Definition>,
    val partOfSpeech: String,
    val synonyms: List<String>
)

@Keep
data class License(
    val name: String,
    val url: String
)
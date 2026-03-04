package labs.creative.dictornarymvvm.domain.mappers

import labs.creative.dictornarymvvm.data.remote.model.WordInfoDto
import labs.creative.dictornarymvvm.data.remote.model.WordSuggestionDto
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.model.WordSuggestion

fun WordSuggestionDto.toDomain(): WordSuggestion {
    return WordSuggestion(
        word = this.word,
        score = this.score,
    )
}

fun WordInfoDto.toDomain(): WordInfo {
    val firstMeaning = meanings?.firstOrNull()
    val firstPhonetic = phonetics?.firstOrNull { !it.text.isNullOrBlank() }

    return WordInfo(
        word = word,
        phonetic = firstPhonetic?.text ?: phonetics?.firstOrNull()?.text ?: "",
        audioUrl = firstPhonetic?.audio?.takeIf { it.isNotBlank() } ?: "",
        partOfSpeech = firstMeaning?.partOfSpeech ?: "",
        definitions = firstMeaning?.definitions?.mapNotNull { it.definition } ?: emptyList(),
        examples = firstMeaning?.definitions?.mapNotNull {
            it.example?.takeIf { ex -> ex.isNotBlank() }
        } ?: emptyList(),
        synonyms = firstMeaning?.synonyms?.filterNotNull() ?: emptyList(),
        antonyms = firstMeaning?.antonyms?.filterNotNull() ?: emptyList(),
    )
}

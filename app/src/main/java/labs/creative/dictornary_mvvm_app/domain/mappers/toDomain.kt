package labs.creative.dictornary_mvvm_app.domain.mappers

import labs.creative.dictornary_mvvm_app.data.remote.model.WordInfoDto
import labs.creative.dictornary_mvvm_app.data.remote.model.WordSuggestionDto
import labs.creative.dictornary_mvvm_app.domain.model.WordInfo
import labs.creative.dictornary_mvvm_app.domain.model.WordSuggestion

fun WordSuggestionDto.toDomain(): WordSuggestion {
    return WordSuggestion(
        word = this.word,
        score = this.score
    )
}

fun WordInfoDto.toDomain(): WordInfo {
    val phoneticsText = phonetics.map { it.text } ?: emptyList()
    val definitionsText = meanings.flatMap { meaning ->
        meaning.definitions.map { it.definition }
    }
    return WordInfo(
        word = this.word,
        phonetics = phoneticsText,
        meanings = definitionsText
    )
}

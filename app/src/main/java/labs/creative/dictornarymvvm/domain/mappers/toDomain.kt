package labs.creative.dictornarymvvm.domain.mappers

import labs.creative.dictornarymvvm.data.remote.model.WordInfoDto
import labs.creative.dictornarymvvm.data.remote.model.WordSuggestionDto
import labs.creative.dictornarymvvm.domain.model.WordSuggestion


fun WordSuggestionDto.toDomain(): WordSuggestion {
    return WordSuggestion(
        word = this.word,
        score = this.score
    )
}

fun WordInfoDto.toDomain(): labs.creative.dictornarymvvm.domain.model.WordInfo {
    val phoneticsText = phonetics.map { it.text } ?: emptyList()
    val definitionsText = meanings.flatMap { meaning ->
        meaning.definitions.map { it.definition }
    }
    return labs.creative.dictornarymvvm.domain.model.WordInfo(
        word = this.word,
        phonetics = phoneticsText,
        meanings = definitionsText
    )
}

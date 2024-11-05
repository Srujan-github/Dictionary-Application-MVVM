package labs.creative.dictornarymvvm.domain.usecase


import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.repository.WordRepository
import javax.inject.Inject

class GetWordInfoUseCase @Inject constructor(
    private val repository: labs.creative.dictornarymvvm.domain.repository.WordRepository
) {
    suspend operator fun invoke(word: String): List<labs.creative.dictornarymvvm.domain.model.WordInfo> {
        return repository.getWordInfo(word)
    }
}

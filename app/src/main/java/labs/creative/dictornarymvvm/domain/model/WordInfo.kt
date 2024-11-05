package labs.creative.dictornarymvvm.domain.model

data class WordInfo(
    val word: String,
    val phonetics: List<String>,  
    val meanings: List<String>   
)

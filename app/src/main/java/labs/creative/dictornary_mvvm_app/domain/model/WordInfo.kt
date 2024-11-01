package labs.creative.dictornary_mvvm_app.domain.model

data class WordInfo(
    val word: String,
    val phonetics: List<String>,  
    val meanings: List<String>   
)

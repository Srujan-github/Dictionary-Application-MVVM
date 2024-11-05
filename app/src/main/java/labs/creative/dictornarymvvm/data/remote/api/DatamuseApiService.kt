package labs.creative.dictornarymvvm.data.remote.api

import labs.creative.dictornarymvvm.data.remote.model.WordSuggestionDto
import retrofit2.http.GET
import retrofit2.http.Query

fun interface DatamuseApiService {
    @GET("words")
    suspend fun getWordSuggestions(@Query("ml") meaning: String): List<WordSuggestionDto>
}

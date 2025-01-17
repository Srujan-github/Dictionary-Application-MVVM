package labs.creative.dictornary_mvvm_app.data.remote.api

import labs.creative.dictornary_mvvm_app.data.remote.model.WordSuggestionDto
import retrofit2.http.GET
import retrofit2.http.Query

fun interface DatamuseApiService {
    @GET("words")
    suspend fun getWordSuggestions(@Query("sp") meaning: String): List<WordSuggestionDto>
}

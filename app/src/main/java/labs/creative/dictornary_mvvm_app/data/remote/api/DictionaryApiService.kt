package labs.creative.dictornary_mvvm_app.data.remote.api

import labs.creative.dictornary_mvvm_app.data.remote.model.WordInfoDto
import retrofit2.http.GET
import retrofit2.http.Path

fun interface DictionaryApiService {
    @GET("{word}")
    suspend fun getWordInfo(@Path("word") word: String): List<WordInfoDto>
}

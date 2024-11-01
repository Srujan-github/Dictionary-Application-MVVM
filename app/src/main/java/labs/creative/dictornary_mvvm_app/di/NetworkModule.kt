package labs.creative.dictornary_mvvm_app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import labs.creative.dictornary_mvvm_app.BuildConfig
import labs.creative.dictornary_mvvm_app.data.remote.api.DatamuseApiService
import labs.creative.dictornary_mvvm_app.data.remote.api.DictionaryApiService
import labs.creative.dictornary_mvvm_app.domain.repository.WordRepository
import labs.creative.dictornary_mvvm_app.domain.usecase.GetWordInfoUseCase
import labs.creative.dictornary_mvvm_app.domain.usecase.GetWordSuggestionsUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Singleton
    @Provides
    @Named("DatamuseRetrofit")
    fun provideDatamuseRetrofit(): Retrofit {
        return Retrofit.Builder().client(OkHttpClient().newBuilder().addInterceptor(provideHttpLogger()).build())
            .baseUrl(BuildConfig.WORD_SEARCH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @Named("DictionaryRetrofit")
    fun provideDictionaryRetrofit(): Retrofit {
        return Retrofit.Builder().client(OkHttpClient().newBuilder().addInterceptor(provideHttpLogger()).build())
            .baseUrl(BuildConfig.DICTIONARY_SEARCH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideDatamuseApiService(@Named("DatamuseRetrofit") retrofit: Retrofit): DatamuseApiService {
        return retrofit.create(DatamuseApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDictionaryApiService(@Named("DictionaryRetrofit") retrofit: Retrofit): DictionaryApiService {
        return retrofit.create(DictionaryApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideWordInfoUseCase(repository: WordRepository):GetWordInfoUseCase{
        return GetWordInfoUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideWordSuggestionUseCase(repository: WordRepository):GetWordSuggestionsUseCase{
        return GetWordSuggestionsUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideHttpLogger(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
    }
    }
}

package labs.creative.dictornarymvvm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import labs.creative.dictornarymvvm.data.remote.api.DatamuseApiService
import labs.creative.dictornarymvvm.data.remote.api.DictionaryApiService
import labs.creative.dictornarymvvm.domain.usecase.GetWordSuggestionsUseCase
import labs.creative.dictornarymvvmapp.BuildConfig

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Singleton
    @Provides
    @Named("DatamuseRetrofit")
    fun provideDatamuseRetrofit(loggingInterceptor: HttpLoggingInterceptor): Retrofit {
        return Retrofit.Builder().client(OkHttpClient().newBuilder().addInterceptor(loggingInterceptor).build())
            .baseUrl(BuildConfig.WORD_SEARCH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @Named("DictionaryRetrofit")
    fun provideDictionaryRetrofit(loggingInterceptor: HttpLoggingInterceptor): Retrofit {
        return Retrofit.Builder().client(OkHttpClient().newBuilder().addInterceptor(loggingInterceptor).build())
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
    fun provideWordInfoUseCase(repository: labs.creative.dictornarymvvm.domain.repository.WordRepository): labs.creative.dictornarymvvm.domain.usecase.GetWordInfoUseCase {
        return labs.creative.dictornarymvvm.domain.usecase.GetWordInfoUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideWordSuggestionUseCase(repository: labs.creative.dictornarymvvm.domain.repository.WordRepository): GetWordSuggestionsUseCase {
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

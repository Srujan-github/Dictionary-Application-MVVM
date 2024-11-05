package labs.creative.dictornarymvvm.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import labs.creative.dictornarymvvm.domain.repository.WordRepository
import labs.creative.dictornary_mvvm_app.domain.repository.WordRepositoryImpl


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindWordRepository(
        wordRepositoryImpl: WordRepositoryImpl
    ): labs.creative.dictornarymvvm.domain.repository.WordRepository
}

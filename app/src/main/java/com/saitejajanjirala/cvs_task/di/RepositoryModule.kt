package com.saitejajanjirala.cvs_task.di

import com.saitejajanjirala.cvs_task.data.repo.SearchRepositoryImpl
import com.saitejajanjirala.cvs_task.domain.repo.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

}
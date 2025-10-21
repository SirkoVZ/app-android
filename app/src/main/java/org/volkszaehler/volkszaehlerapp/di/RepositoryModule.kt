package org.volkszaehler.volkszaehlerapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.volkszaehler.volkszaehlerapp.data.repository.ChannelRepositoryImpl
import org.volkszaehler.volkszaehlerapp.domain.repository.ChannelRepository
import javax.inject.Singleton

/**
 * Dagger Hilt module for repository bindings
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds ChannelRepositoryImpl to ChannelRepository interface
     */
    @Binds
    @Singleton
    abstract fun bindChannelRepository(
        channelRepositoryImpl: ChannelRepositoryImpl
    ): ChannelRepository
}

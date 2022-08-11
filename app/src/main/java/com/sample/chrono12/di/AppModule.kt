package com.sample.chrono12.di

import android.app.Application
import androidx.room.Room
import com.sample.chrono12.data.database.WatchShopDatabase
import com.sample.chrono12.data.entities.User
import com.sample.chrono12.data.repository.UserRepository
import com.sample.chrono12.data.repository.WatchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideWatchShopDatabase(app : Application) : WatchShopDatabase {
        return Room.databaseBuilder(
            app,
            WatchShopDatabase::class.java,
            "watch_shop"
        ).createFromAsset("watch_shop_db").build()
    }

    @Singleton
    @Provides
    fun provideWatchRepository(db: WatchShopDatabase) : WatchRepository{
        return WatchRepository(db.watchDao)
    }

    @Singleton
    @Provides
    fun providesUserRepository(db: WatchShopDatabase): UserRepository{
        return UserRepository(db.userDao)
    }
}
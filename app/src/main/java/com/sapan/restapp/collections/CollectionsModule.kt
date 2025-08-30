package com.sapan.restapp.collections

import android.content.Context
import androidx.room.Room
import com.sapan.restapp.collections.dao.CollectionDao
import com.sapan.restapp.collections.dao.RequestDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CollectionsModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): RestDatabase {
        return Room.databaseBuilder(
            context,
            RestDatabase::class.java,
            "rest_app.db"
        ).build()
    }

    @Provides
    fun provideCollectionDao(database: RestDatabase): CollectionDao {
        return database.collectionDao()
    }

    @Provides
    fun provideRequestDao(database: RestDatabase): RequestDao {
        return database.requestDao()
    }
}
package com.example.ocr_v3.di

import android.content.Context
import androidx.room.Room
import com.example.ocr_v3.data.local.AppDatabase
import com.example.ocr_v3.data.local.CardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule{

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context = context ,
            AppDatabase::class.java ,
            "cards"
        ).build()
    }

    @Singleton
    @Provides
    fun provideCardDao(db : AppDatabase) : CardDao{
        return db.dao
    }
}
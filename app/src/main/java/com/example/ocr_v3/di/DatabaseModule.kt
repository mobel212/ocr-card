package com.example.ocr_v3.di

import android.content.Context
import androidx.room.Room
import com.example.ocr_v3.data.local.AppDatabase
import com.example.ocr_v3.data.local.CardDao
import com.example.ocr_v3.security.DatabaseKeyManager
import com.example.ocr_v3.security.DatabaseKeyResult
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabaseKeyManager(
        @ApplicationContext context: Context
    ): DatabaseKeyManager = DatabaseKeyManager(context)

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext context: Context,
        keyManager: DatabaseKeyManager
    ): AppDatabase {
        return when (val result = keyManager.getDatabasePassphrase()) {
            is DatabaseKeyResult.Success -> {
                val factory = SupportFactory(result.passphrase)
                val db = Room.databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    name = "cards"
                )
                    .openHelperFactory(factory)
                    .build()

                // Optional: wipe passphrase from memory after Room opens
                keyManager.wipePassphrase(result.passphrase)
                db
            }

            DatabaseKeyResult.RecoveryNeeded -> {
                // The Keystore key was invalidated. Delete the unreadable DB.
                context.deleteDatabase("cards")

                // Retry once after cleanup
                val retry = keyManager.getDatabasePassphrase()
                check(retry is DatabaseKeyResult.Success) {
                    "Database recovery failed even after clearing key material."
                }

                val factory = SupportFactory(retry.passphrase)
                Room.databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    name = "cards"
                )
                    .openHelperFactory(factory)
                    .build()
            }
        }
    }

    @Singleton
    @Provides
    fun provideCardDao(db: AppDatabase): CardDao {
        return db.dao
    }
}
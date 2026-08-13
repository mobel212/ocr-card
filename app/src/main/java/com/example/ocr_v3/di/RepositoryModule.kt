package com.example.ocr_v3.di

import com.example.ocr_v3.data.nfc.NfcReaderImpl
import com.example.ocr_v3.data.repository.CardRepositoryImpl
import com.example.ocr_v3.domain.repository.CardRepository
import com.example.ocr_v3.domain.repository.NfcReader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        cardRepositoryImpl: CardRepositoryImpl
    ) : CardRepository

    @Binds
    @Singleton
    abstract fun bindNfcReader(
        nfcReaderImpl: NfcReaderImpl
    ) : NfcReader
}
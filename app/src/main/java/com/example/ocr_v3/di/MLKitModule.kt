package com.example.ocr_v3.di

import com.example.ocr_v3.data.mlkit.TextRecognizer
import com.example.ocr_v3.domain.usecase.ParseCardUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MLKitModule{

    @Singleton
    @Provides
    fun provideTextRecognizer() : TextRecognizer {
        return TextRecognizer()
    }

    @Singleton
    @Provides
    fun provideParseCardUseCase() : ParseCardUseCase{
        return ParseCardUseCase()
    }


}
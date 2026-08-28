package com.example.ocr_v3

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

@HiltAndroidApp
class OcrApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // 1. Aggressively remove Android's crippled built-in Bouncy Castle
            Security.removeProvider("BC")

            // 2. Insert the full, powerful Bouncy Castle provider we added in build.gradle
            Security.insertProviderAt(BouncyCastleProvider(), 1)
        } catch (t: Throwable) {
            android.util.Log.e("OcrApplication", "Failed to register BouncyCastle", t)
        }
    }
}
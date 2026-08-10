package com.example.ocr_v3.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

sealed class DatabaseKeyResult {
    data class Success(val passphrase: ByteArray) : DatabaseKeyResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Success) return false
            return passphrase.contentEquals(other.passphrase)
        }
        override fun hashCode(): Int = passphrase.contentHashCode()
    }

    /**
     * The Keystore key is unusable (invalidated, corrupted, or missing)
     * and the encrypted passphrase cannot be recovered.
     *
     * The caller must decide:
     * - Delete the database file and recreate it?
     * - Show an error to the user?
     * - Restore from backup?
     */
    object RecoveryNeeded : DatabaseKeyResult()
}

class DatabaseKeyManager(context: Context) {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "db_passphrase_protector"
        private const val PREFS_NAME = "db_security_prefs"
        private const val PREF_ENCRYPTED_PASS = "enc_passphrase"
        private const val PREF_IV = "enc_iv"
        private const val KEY_SIZE = 256
        private const val GCM_TAG_LENGTH = 128
        private const val PASSPHRASE_SIZE = 32
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val BASE64_FLAGS = Base64.NO_WRAP
    }

    // -----------------------------------------------------------------
    // Lazy-loaded dependencies — no repeated work
    // -----------------------------------------------------------------
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Public API.
     *
     * Returns [DatabaseKeyResult.Success] with the passphrase,
     * or [DatabaseKeyResult.RecoveryNeeded] if the key material is lost.
     *
     * The caller is responsible for deciding what to do on recovery.
     */
    fun getDatabasePassphrase(): DatabaseKeyResult {
        return try {
            loadOrCreatePassphrase()
//        } catch (e: javax.crypto) {
//            DatabaseKeyResult.RecoveryNeeded
        } catch (e: AEADBadTagException) {
            DatabaseKeyResult.RecoveryNeeded
        } catch (e: javax.crypto.BadPaddingException) {
            DatabaseKeyResult.RecoveryNeeded
        }
        // Everything else (NullPointerException, OutOfMemoryError, etc.)
        // propagates up and crashes during development so you notice the bug.
    }

    /**
     * Optional: wipes the passphrase from memory after Room has finished
     * opening the database. Banking-grade apps often do this.
     *
     * Usage:
     *   val result = keyManager.getDatabasePassphrase()
     *   if (result is Success) {
     *       val factory = SupportFactory(result.passphrase)
     *       val db = Room.databaseBuilder(...).openHelperFactory(factory).build()
     *       result.passphrase.fill(0) // <-- wipe
     *   }
     */
    fun wipePassphrase(passphrase: ByteArray) {
        passphrase.fill(0)
    }

    // -----------------------------------------------------------------
    // Core flow: exists? → decrypt | generate → encrypt → store → return
    // -----------------------------------------------------------------
    private fun loadOrCreatePassphrase(): DatabaseKeyResult.Success {
        ensureKeystoreKeyExists()

        val encrypted = getPref(PREF_ENCRYPTED_PASS)
        val iv = getPref(PREF_IV)

        val passphrase = if (encrypted != null && iv != null) {
            decryptPassphrase(encrypted, iv)
        } else {
            val raw = generatePassphrase()
            val (encBytes, ivBytes) = encryptPassphrase(raw)
            savePref(PREF_ENCRYPTED_PASS, encBytes)
            savePref(PREF_IV, ivBytes)
            raw
        }

        return DatabaseKeyResult.Success(passphrase)
    }

    private fun generatePassphrase(): ByteArray {
        val random = SecureRandom()
        return ByteArray(PASSPHRASE_SIZE).apply { random.nextBytes(this) }
    }

    // -----------------------------------------------------------------
    // Keystore key management
    // -----------------------------------------------------------------
    private fun ensureKeystoreKeyExists() {
        if (keyStore.containsAlias(KEY_ALIAS)) return

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    // -----------------------------------------------------------------
    // Crypto
    // -----------------------------------------------------------------
    private fun encryptPassphrase(passphrase: ByteArray): Pair<ByteArray, ByteArray> {
        val secretKey = (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry)
            .secretKey

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv
        val encrypted = cipher.doFinal(passphrase)
        return encrypted to iv
    }

    private fun decryptPassphrase(encrypted: ByteArray, iv: ByteArray): ByteArray {
        val secretKey = (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry)
            .secretKey

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        return cipher.doFinal(encrypted)
    }

    // -----------------------------------------------------------------
    // Storage: SharedPreferences holds ONLY encrypted garbage, no line breaks
    // -----------------------------------------------------------------
    private fun savePref(key: String, bytes: ByteArray) {
        prefs.edit()
            .putString(key, Base64.encodeToString(bytes, BASE64_FLAGS))
            .apply()
    }

    private fun getPref(key: String): ByteArray? {
        val str = prefs.getString(key, null) ?: return null
        return Base64.decode(str, BASE64_FLAGS)
    }

    /**
     * Exposed so the caller can wipe everything when RecoveryNeeded is returned.
     */
    fun wipeAllKeyMaterial() {
        prefs.edit().clear().apply()

        try {
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
        } catch (_: Exception) { /* best effort */ }
    }
}
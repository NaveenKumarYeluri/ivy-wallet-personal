package com.ivy.data.backup

import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoVault {
    private val MAGIC_HEADER = byteArrayOf(0x49, 0x56, 0x59, 0x42) // "IVYB"
    private const val VERSION: Byte = 0x01
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 12
    private const val TAG_LENGTH_BITS = 128
    private const val PBKDF2_ITERATIONS = 65536
    private const val KEY_LENGTH_BITS = 256

    private val secureRandom = SecureRandom()

    fun encrypt(data: ByteArray, password: CharArray): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        secureRandom.nextBytes(salt)

        val iv = ByteArray(IV_LENGTH)
        secureRandom.nextBytes(iv)

        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)

        val ciphertext = cipher.doFinal(data)

        val buffer = ByteBuffer.allocate(MAGIC_HEADER.size + 1 + SALT_LENGTH + IV_LENGTH + ciphertext.size)
        buffer.put(MAGIC_HEADER)
        buffer.put(VERSION)
        buffer.put(salt)
        buffer.put(iv)
        buffer.put(ciphertext)

        return buffer.array()
    }

    fun decrypt(vaultBytes: ByteArray, password: CharArray): ByteArray {
        val minLength = MAGIC_HEADER.size + 1 + SALT_LENGTH + IV_LENGTH + (TAG_LENGTH_BITS / 8)
        if (vaultBytes.size < minLength) {
            throw IllegalArgumentException("Invalid vault backup: File is too small or corrupted.")
        }

        val buffer = ByteBuffer.wrap(vaultBytes)

        val magic = ByteArray(MAGIC_HEADER.size)
        buffer.get(magic)
        if (!magic.contentEquals(MAGIC_HEADER)) {
            throw IllegalArgumentException("Invalid vault backup: Not an Ivy Wallet encrypted vault file.")
        }

        val version = buffer.get()
        if (version != VERSION) {
            throw IllegalArgumentException("Unsupported vault version: $version")
        }

        val salt = ByteArray(SALT_LENGTH)
        buffer.get(salt)

        val iv = ByteArray(IV_LENGTH)
        buffer.get(iv)

        val ciphertext = ByteArray(buffer.remaining())
        buffer.get(ciphertext)

        val key = deriveKey(password, salt)

        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            return cipher.doFinal(ciphertext)
        } catch (e: Exception) {
            throw SecurityException("Incorrect vault password or corrupted backup file.", e)
        }
    }

    private fun deriveKey(password: CharArray, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        return try {
            val secretKey = factory.generateSecret(spec)
            SecretKeySpec(secretKey.encoded, "AES")
        } finally {
            spec.clearPassword()
        }
    }
}

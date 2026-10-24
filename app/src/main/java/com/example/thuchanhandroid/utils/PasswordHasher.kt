package com.example.thuchanhandroid.utils

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordHasher {

    private const val DELIMITER = ":"
    private const val SALT_LENGTH = 16

    fun hash(password: String): String {
        val salt = generateSalt()
        return salt + DELIMITER + sha256(salt + password)
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(DELIMITER)
        if (parts.size != 2) return false
        val (salt, hash) = parts
        return sha256(salt + password) == hash
    }

    private fun generateSalt(): String {
        val bytes = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    private fun sha256(input: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
            .toHex()

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}

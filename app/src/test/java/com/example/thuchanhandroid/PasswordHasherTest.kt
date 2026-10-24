package com.example.thuchanhandroid

import com.example.thuchanhandroid.utils.PasswordHasher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun hash_samePassword_producesDifferentHashes() {
        val hash1 = PasswordHasher.hash("admin123")
        val hash2 = PasswordHasher.hash("admin123")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun verify_correctPassword_returnsTrue() {
        val stored = PasswordHasher.hash("admin123")
        assertTrue(PasswordHasher.verify("admin123", stored))
    }

    @Test
    fun verify_wrongPassword_returnsFalse() {
        val stored = PasswordHasher.hash("admin123")
        assertFalse(PasswordHasher.verify("wrong-password", stored))
    }

    @Test
    fun verify_malformedStoredValue_returnsFalse() {
        assertFalse(PasswordHasher.verify("admin123", "malformed"))
    }
}

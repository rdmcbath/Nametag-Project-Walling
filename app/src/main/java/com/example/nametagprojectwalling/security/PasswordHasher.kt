package com.example.nametagprojectwalling.security

import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.security.SecureRandom
import android.util.Base64

class PasswordHasher {
    companion object {
        private const val SALT_LENGTH = 16
        private const val HASH_LENGTH = 32

        fun hashPassword(password: String): String {
            val salt = ByteArray(SALT_LENGTH).apply {
                SecureRandom().nextBytes(this)
            }

            val hash = ByteArray(HASH_LENGTH)
            val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withParallelism(4)
                .withMemoryAsKB(65536)
                .withIterations(3)
                .build()

            val generator = Argon2BytesGenerator()
            generator.init(params)
            generator.generateBytes(password.toByteArray(), hash)

            // Combine salt and hash for storage
            val combined = ByteArray(salt.size + hash.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(hash, 0, combined, salt.size, hash.size)

            return Base64.encodeToString(combined, Base64.NO_WRAP)
        }

        fun verifyPassword(password: String, storedHash: String): Boolean {
            val combined = Base64.decode(storedHash, Base64.NO_WRAP)
            val salt = ByteArray(SALT_LENGTH)
            val hash = ByteArray(HASH_LENGTH)

            System.arraycopy(combined, 0, salt, 0, salt.size)
            System.arraycopy(combined, salt.size, hash, 0, hash.size)

            val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withParallelism(4)
                .withMemoryAsKB(65536)
                .withIterations(3)
                .build()

            val generator = Argon2BytesGenerator()
            generator.init(params)

            val checkHash = ByteArray(HASH_LENGTH)
            generator.generateBytes(password.toByteArray(), checkHash)

            return hash.contentEquals(checkHash)
        }
    }
}

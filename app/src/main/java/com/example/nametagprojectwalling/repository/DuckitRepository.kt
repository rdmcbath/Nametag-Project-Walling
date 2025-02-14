package com.example.nametagprojectwalling.repository

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nametagprojectwalling.api.DuckitApi
import com.example.nametagprojectwalling.data.local.UserDao
import com.example.nametagprojectwalling.data.local.UserEntity
import com.example.nametagprojectwalling.data.model.CreatePostRequest
import com.example.nametagprojectwalling.data.model.Post
import com.example.nametagprojectwalling.data.model.SignInRequest
import com.example.nametagprojectwalling.data.model.SignInResponse
import com.example.nametagprojectwalling.data.model.VoteResponse
import com.example.nametagprojectwalling.security.PasswordHasher
import retrofit2.HttpException
import javax.inject.Inject

interface DuckitRepository {
    suspend fun signIn(email: String, password: String): Result<SignInResponse>
    suspend fun signUp(email: String, password: String): Result<SignInResponse>
    suspend fun getPosts(): Result<List<Post>>
    suspend fun upvotePost(postId: String): Result<VoteResponse>
    suspend fun downvotePost(postId: String): Result<VoteResponse>
    suspend fun createPost(headline: String, imageUrl: String): Result<Unit>
}

class DuckitRepositoryImpl @Inject constructor(
    private val api: DuckitApi,
    private val userDao: UserDao,
    private val sharedPreferences: SharedPreferences
) : DuckitRepository {

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun <T> makeApiCall(
        requiresAuth: Boolean = true,
        call: suspend (String?) -> T
    ): Result<T> {
        return try {
            if (requiresAuth) {
                val token = getStoredToken()?.trim()
                println("Debug: Token for API call: ${token?.take(10)}...")

                if (token == null) {
                    return Result.failure(Exception("User not authenticated"))
                }

                val authHeader = "Bearer $token"
                println("Debug: Auth header: ${authHeader.take(15)}...")
                Result.success(call(authHeader))
            } else {
                Result.success(call(null))
            }
        } catch (e: HttpException) {
            println("Debug: Response code: ${e.code()}")
            println("Debug: Response error body: ${e.response()?.errorBody()?.string()}")
            Result.failure(e)
        } catch (e: Exception) {
            println("Debug: General error: ${e.message}")
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun signIn(email: String, password: String): Result<SignInResponse> {
        return try {
            val request = SignInRequest(email, password)
            try {
                // Try to sign in first
                val response = makeApiCall(requiresAuth = false) {
                    api.signIn(request)
                }.getOrThrow()

                storeAuthToken(response.token)
                storeUserCredentials(email, password)

                Result.success(response)
            } catch (e: HttpException) {
                when (e.code()) {
                    404 -> signUp(email, password)
                    403 -> signUp(email, password)
                    else -> Result.failure(Exception("Network error occurred"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("An unexpected error occurred"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun signUp(email: String, password: String): Result<SignInResponse> {
        return try {
            val request = SignInRequest(email, password)
            val response = makeApiCall(requiresAuth = false) {
                api.signUp(request)
            }.getOrThrow()

            storeAuthToken(response.token)
            storeUserCredentials(email, password)

            Result.success(response)
        } catch (e: HttpException) {
            when (e.code()) {
                409 -> Result.failure(Exception("Account already exists"))
                else -> Result.failure(Exception("Network error occurred"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("An unexpected error occurred"))
        }
    }

    // The user's email and hashed password are stored in the database
    private suspend fun storeUserCredentials(email: String, password: String) {
        val hashedPassword = PasswordHasher.hashPassword(password)
        userDao.insertUser(
            UserEntity(
                email = email,
                hashedPassword = hashedPassword,
                lastLoginTimestamp = System.currentTimeMillis()
            )
        )
    }

    // The user's refresh token is stored securely in EncryptedSharedPreferences
    @RequiresApi(Build.VERSION_CODES.O)
    private fun storeAuthToken(token: String) {
        try {
            val trimmedToken = token.trim()
            editor.putString(AUTH_TOKEN_KEY, trimmedToken)
            editor.apply()

            val storedToken = sharedPreferences.getString(AUTH_TOKEN_KEY, null)?.trim()
            println("Debug: Verification - stored token: ${storedToken?.take(10)}...")
        } catch (e: Exception) {
            println("Debug: Error storing token: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStoredToken(): String? {
        return try {
            val token = sharedPreferences.getString(AUTH_TOKEN_KEY, null)?.trim()
            println("Debug: Getting token from storage: ${token?.take(10)}...")
            token
        } catch (e: Exception) {
            println("Debug: Error retrieving token: ${e.message}")
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getPosts(): Result<List<Post>> {
        return makeApiCall(requiresAuth = false) {
            println("Debug: Starting getPosts API call")
            val response = api.getPosts()
            println("Debug: getPosts API call successful, posts count: ${response.posts.size}")
            response.posts
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun upvotePost(postId: String): Result<VoteResponse> {
        return makeApiCall { authHeader ->
            println("Debug: Making request to: https://nametag-duckit-2.uc.r.appspot.com/posts/$postId/upvote")
            api.upvotePost(
                postId = postId,
                auth = "Bearer <token>"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun downvotePost(postId: String): Result<VoteResponse> {
        return makeApiCall { authHeader ->
            println("Debug: Making request to: https://nametag-duckit-2.uc.r.appspot.com/posts/$postId/upvote")
            api.downvotePost(
                postId = postId,
                auth = "Bearer <token>"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createPost(headline: String, imageUrl: String): Result<Unit> {
        return makeApiCall { authHeader ->
            val request = CreatePostRequest(headline, imageUrl)
            api.createPost(authHeader!!, request)
        }
    }

    companion object {
        private const val AUTH_TOKEN_KEY = "auth_token"
    }
}

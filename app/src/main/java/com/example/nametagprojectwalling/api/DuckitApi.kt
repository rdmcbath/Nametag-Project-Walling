package com.example.nametagprojectwalling.api

import com.example.nametagprojectwalling.data.model.CreatePostRequest
import com.example.nametagprojectwalling.data.model.PostsResponse
import com.example.nametagprojectwalling.data.model.SignInRequest
import com.example.nametagprojectwalling.data.model.SignInResponse
import com.example.nametagprojectwalling.data.model.VoteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// API Interface
interface DuckitApi {
    @POST("signin")
    suspend fun signIn(@Body request: SignInRequest): SignInResponse

    @POST("signup")
    suspend fun signUp(@Body request: SignInRequest): SignInResponse

    @GET("posts")
    suspend fun getPosts(): PostsResponse

    @POST("posts/{id}/upvote")
    suspend fun upvotePost(
        @Path("id") postId: String,
        @Header("Authorization") auth: String = "Bearer <token>"
    ): VoteResponse

    @POST("posts/{id}/downvote")
    suspend fun downvotePost(
        @Path("id", encoded = true) postId: String,
        @Header("Authorization") auth: String = "Bearer <token>"
    ): VoteResponse

    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") auth: String,
        @Body request: CreatePostRequest
    )
}
package com.example.nametagprojectwalling.data.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

data class PostsResponse(
    @SerializedName("Posts")
    val posts: List<Post>
)
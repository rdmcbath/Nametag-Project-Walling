package com.example.nametagprojectwalling.state

import com.example.nametagprojectwalling.data.model.Post

data class PostsScreenState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
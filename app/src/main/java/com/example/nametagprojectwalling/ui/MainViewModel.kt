package com.example.nametagprojectwalling.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nametagprojectwalling.data.model.User
import com.example.nametagprojectwalling.repository.DuckitRepository
import com.example.nametagprojectwalling.state.AuthUIState
import com.example.nametagprojectwalling.state.PostsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DuckitRepository
) : ViewModel() {

    private val _postsScreenState = MutableStateFlow(PostsScreenState())
    val postsScreenState: StateFlow<PostsScreenState> = _postsScreenState.asStateFlow()

    private val _authUIState = MutableStateFlow<AuthUIState>(AuthUIState.Unauthenticated(null))
    val authUIState: StateFlow<AuthUIState> = _authUIState.asStateFlow()

    private val _voteCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val voteCounts: StateFlow<Map<String, Int>> = _voteCounts.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _postsScreenState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = repository.getPosts()
                result.fold(
                    onSuccess = { posts ->
                        _postsScreenState.update { currentState ->
                            currentState.copy(
                                posts = posts,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _postsScreenState.update { currentState ->
                            currentState.copy(
                                posts = emptyList(),
                                isLoading = false,
                                error = exception.message ?: "Failed to load posts"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _postsScreenState.update { currentState ->
                    currentState.copy(
                        posts = emptyList(),
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authUIState.value = AuthUIState.Unauthenticated(null)

            try {
                val result = repository.signIn(email, password)
                result.fold(
                    onSuccess = { signInResponse ->
                        val user = User(email = email, token = signInResponse.token)
                        _authUIState.value = AuthUIState.Authenticated(user)
                    },
                    onFailure = { exception ->
                        _authUIState.value = AuthUIState.Unauthenticated(exception.message)
                    }
                )
            } catch (e: Exception) {
                _authUIState.value = AuthUIState.Unauthenticated(e.message)
            }
        }
    }

        fun upvotePost(postId: String) {
            viewModelScope.launch {
                try {
                    val result = repository.upvotePost(postId)

                    result.fold(
                        onSuccess = { voteResponse ->
                            _voteCounts.update { it + (postId to voteResponse.upvotes) }
                        },
                        onFailure = { exception ->
                            if (exception.message?.contains("not authenticated") == true) {
                                _authUIState.value =
                                    AuthUIState.Unauthenticated("not authenticated")
                                _postsScreenState.update { it.copy(error = "not authenticated") }
                            } else {
                                _postsScreenState.update { it.copy(error = exception.message) }
                            }
                        }
                    )
                } catch (e: Exception) {
                    _postsScreenState.update { it.copy(error = e.message) }
                }
            }
        }

        fun downvotePost(postId: String) {
            viewModelScope.launch {
                try {
                    val result = repository.downvotePost(postId)
                    result.fold(
                        onSuccess = { voteResponse ->
                            _voteCounts.update { it + (postId to voteResponse.upvotes) }
                        },
                        onFailure = { exception ->
                            if (exception.message?.contains("not authenticated") == true) {
                                _postsScreenState.update {
                                    it.copy(error = "not authenticated")
                                }
                            } else {
                                _postsScreenState.update {
                                    it.copy(error = exception.message)
                                }
                            }
                        }
                    )
                } catch (e: Exception) {
                    _postsScreenState.update {
                        it.copy(error = e.message)
                    }
                }
            }
        }

        fun createPost(headline: String, imageUrl: String) {
            viewModelScope.launch {
                try {
                    val result = repository.createPost(headline, imageUrl)
                    result.fold(
                        onSuccess = {
                            showToast("Post created successfully!")
                            loadPosts()
                        },
                        onFailure = { exception ->
                            _postsScreenState.update {
                                it.copy(error = exception.message ?: "Failed to create post")
                            }
                        }
                    )
                } catch (e: Exception) {
                    _postsScreenState.update {
                        it.copy(error = e.message ?: "Failed to create post")
                    }
                }
            }
        }

        private fun showToast(message: String) {
            _toastMessage.value = message
        }

        fun clearToast() {
            _toastMessage.value = null
        }

        fun clearAuthError() {
            _postsScreenState.update { currentState ->
                currentState.copy(error = null)
            }
        }
    }

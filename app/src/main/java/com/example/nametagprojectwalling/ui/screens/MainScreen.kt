package com.example.nametagprojectwalling.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nametagprojectwalling.state.AuthUIState
import com.example.nametagprojectwalling.ui.MainViewModel
import com.example.nametagprojectwalling.ui.components.AddPostDialog
import com.example.nametagprojectwalling.ui.components.PostList
import com.example.nametagprojectwalling.ui.components.SignInDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val postsScreenState by viewModel.postsScreenState.collectAsState()
    val authUIState by viewModel.authUIState.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var showSignInDialog by remember { mutableStateOf(false) }
    var showAddPostDialog by remember { mutableStateOf(false) }

    LaunchedEffect(postsScreenState.error) {
        if (postsScreenState.error == "not authenticated") {
            showSignInDialog = true
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = com.example.nametagprojectwalling.R.string.app_bar_name),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (authUIState is AuthUIState.Unauthenticated) {
                        showSignInDialog = true
                    } else {
                        showAddPostDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Post"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                postsScreenState.isLoading -> {
                    LoadingScreen()
                }
                postsScreenState.error != null && postsScreenState.error != "not authenticated" -> {
                    ErrorScreen(message = postsScreenState.error!!)
                }
                else -> {
                    PostList(
                        posts = postsScreenState.posts,
                        onUpvote = viewModel::upvotePost,
                        onDownvote = viewModel::downvotePost,
                    )
                }
            }
        }
    }

    if (showAddPostDialog) {
        AddPostDialog(
            onDismiss = { showAddPostDialog = false },
            onSubmit = { headline, imageUrl ->
                viewModel.createPost(headline, imageUrl)
                showAddPostDialog = false
            }
        )
    }

    if (showSignInDialog) {
        SignInDialog(
            onDismiss = {
                showSignInDialog = false
                viewModel.clearAuthError()
            },
            onSignIn = { email, password ->
                viewModel.signIn(email, password)
                showSignInDialog = false
            }
        )
    }
}

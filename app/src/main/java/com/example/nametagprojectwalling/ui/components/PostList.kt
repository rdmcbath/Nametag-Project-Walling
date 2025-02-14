package com.example.nametagprojectwalling.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nametagprojectwalling.data.model.Post
import com.example.nametagprojectwalling.ui.MainViewModel

@Composable
fun PostList(
    posts: List<Post>,
    onUpvote: (String) -> Unit,
    onDownvote: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(posts) { post ->
            val voteCounts by viewModel.voteCounts.collectAsState()

            PostListItem(
                post = post,
                upvoteCount = voteCounts[post.id] ?: post.upvotes,
                onUpvote = { onUpvote(post.id) },
                onDownvote = { onDownvote(post.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

package com.example.agora.screens.postDetail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.agora.model.data.Comment
import com.example.agora.model.data.User
import com.example.agora.model.repository.CommentUtils
import com.example.agora.model.repository.WishlistUtils
import com.example.agora.ui.components.ImageCarousel
import com.example.agora.ui.components.MapScreen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(viewModel: PostDetailViewModel = viewModel(), navController: NavController) {
    val _post by viewModel.post.collectAsState()
    val post = _post
    val _user by viewModel.user.collectAsState()
    val user = _user
    val inWishlist by viewModel.inWishlist.collectAsState()
    val scrollState = rememberScrollState() // Enables scrolling
    val context = LocalContext.current

    val currentUser = FirebaseAuth.getInstance().currentUser

    var showContactModal by remember { mutableStateOf(false) }
    var showReportModal by remember { mutableStateOf(false) }

    var commentField = viewModel.commentField.collectAsState()
    val comments = viewModel.comments.collectAsState()

    if (showContactModal && user != null) {
        ContactModal(user, { showContactModal = false })
    } else if (showReportModal && user != null) {
        ReportModal(user, { showReportModal = false })
    }

    LaunchedEffect(navController.currentBackStackEntry) {
        if (post != null) viewModel.checkIfPostInWishlist(post.postId)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopAppBar(
            title = { Text(
                post?.title ?: "",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            ) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.verticalScroll(scrollState)) {
            if (post != null) {
                ImageCarousel(post.images)
                Column(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth()
                ) {
                    Column (verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$ " + String.format("%.2f", post.price),
                                fontSize = 21.sp
                            )
                            IconButton(onClick = {
                                currentUser?.uid?.let {
                                    if (!inWishlist) {
                                        WishlistUtils.addToWishList(
                                            currentUser.uid,
                                            post.postId
                                        ) { added ->
                                            viewModel.checkIfPostInWishlist(post.postId)
                                        }
                                    } else {
                                        WishlistUtils.removeFromWishList(
                                            currentUser.uid,
                                            post.postId
                                        ) { removed ->
                                            viewModel.checkIfPostInWishlist(post.postId)
                                        }
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = if (inWishlist) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Add to wishlist",
                                    tint = if (inWishlist) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                        }
                        Text(
                            text = "DESCRIPTION",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = post.description,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                    Column {
                        Text(
                            text = "POSTED BY",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row {
                                AsyncImage(
                                    model = user?.profileImage,
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(27.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(Modifier.size(10.dp))

                                Text(
                                    text = user?.fullName ?: "Unknown",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            TextButton(onClick = { showReportModal = true }) {
                                Text(
                                    text = "Report post",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                    OutlinedButton (
                        onClick = { showContactModal = true },
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(10.dp, 14.dp),
                        modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Contact seller",
                            fontSize = 16.sp
                        )
                    }
                    MapScreen(post.address)
                    HorizontalDivider(thickness = 1.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Comments",
                            fontSize = 21.sp
                        )
                        TextField(
                            value = commentField.value,
                            onValueChange = { viewModel.updateComment(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Leave a comment...") },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(16.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (currentUser != null) {
                                        CommentUtils.createComment(
                                            postId = post.postId,
                                            userId = currentUser.uid,
                                            text = commentField.value,
                                            onSuccess = {
                                                viewModel.updateComment("")
                                                viewModel.fetchComments(post.postId)
                                            },
                                            onFailure = { Toast.makeText(context, "Failed to create comment", Toast.LENGTH_SHORT).show() },
                                        )
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Create comment"
                                    )
                                }
                            },
                        )
                        comments.value.forEach { comment ->
                            CommentItem(
                                viewModel,
                                comment,
                                { username ->
                                    viewModel.updateComment("${commentField.value}@${username} ")
                                }
                            )
                        }
                    }
                }
            } else {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ContactModal(user: User, onDismiss: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    AlertDialog(
        shape = RoundedCornerShape(21.dp),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(user.email))
                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy Email to Clipboard")
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    AsyncImage(
                        model = user.profileImage,
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(27.dp))
                    )
                    Text(
                        text = user.fullName,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = user.email,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Column (
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ABOUT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = user.bio,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        },
    )
}

@Composable
fun ReportModal(user: User, onDismiss: () -> Unit) {
    AlertDialog(
        shape = RoundedCornerShape(21.dp),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onDismiss,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Report")
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Report post",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Are you sure you want to report this post? This action cannot be undone.",
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
    )
}

@Composable
fun CommentItem(
    viewModel: PostDetailViewModel,
    comment: Comment,
    replyOnClick: (String) -> Unit,
) {
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(comment.userId) {
        viewModel.fetchUser(comment.userId, { fetchedUser ->
            if (fetchedUser == null) {
                Log.e(
                    "ProfileDetailScreen",
                    "User ${comment.userId} not found for comment ${comment.commentId}"
                )
            } else {
                user = fetchedUser
            }
        })
    }

    user?.let { user ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                .padding(21.dp)
        ) {
            Row {
                AsyncImage(
                    model = user.profileImage,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(27.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding()
                )

                Spacer(Modifier.size(10.dp))

                Column (verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "${user.fullName} | @${user.username}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = comment.text
                    )

                    Text(
                        text = "Reply",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { replyOnClick(user.username) }
                    )
                }
            }
        }
    }
}

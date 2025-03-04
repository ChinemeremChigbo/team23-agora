package com.example.agora.screens.postDetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.agora.model.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(viewModel: PostDetailViewModel = viewModel(), navController: NavController) {
    var showContactModal by remember { mutableStateOf(false) }
    var showReportModal by remember { mutableStateOf(false) }
    val _post by viewModel.post.collectAsState()
    val post = _post
    val _user by viewModel.user.collectAsState()
    val user = _user

    if (showContactModal && user != null) {
        ContactModal(user, { showContactModal = false })
    } else if (showReportModal && user != null) {
        ReportModal(user, { showReportModal = false })
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
        Column(verticalArrangement = Arrangement.Center) {
            if (post != null) {
                AsyncImage(
                    model = post.images[0],
                    contentDescription = "Product image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .background(Color.LightGray)
                        .height(240.dp)
                        .fillMaxWidth()
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.padding(32.dp).fillMaxWidth()
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
                            // TODO (jennifer): wire up when wishlist is ready
                            IconButton(onClick = { }) {
                                Icon(
                                    Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Add to wishlist",
                                    modifier = Modifier.size(32.dp)
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
                            text = "Post detail Screen for ${post.postId}" + post.description,
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
                                    model = post.images[0],
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(27.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(Modifier.size(10.dp))

                                Text(
                                    text = "test user",
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
                    android.widget.Toast.makeText(context, "Copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(16.dp),
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

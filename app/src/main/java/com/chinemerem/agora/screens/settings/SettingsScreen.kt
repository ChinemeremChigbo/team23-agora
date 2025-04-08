package com.chinemerem.agora.screens.settings

import AppearanceViewModel
import UpdatePasswordScreen
import UpdatePasswordViewModel
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.chinemerem.agora.model.data.User
import com.chinemerem.agora.model.repository.ProfileSettingRepository
import com.chinemerem.agora.model.repository.UserRepository
import com.chinemerem.agora.model.util.AccountAuthUtil
import com.chinemerem.agora.screens.settings.appearance.AppearanceScreen
import com.chinemerem.agora.screens.settings.appearance.AppearanceViewModelFactory
import com.chinemerem.agora.screens.settings.profile.ProfileScreen
import com.chinemerem.agora.screens.settings.profile.ProfileViewModel
import com.chinemerem.agora.util.uploadImageToS3
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    auth: FirebaseAuth,
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf(User()) }
    // Refetch user object everytime it navigate to this page
    LaunchedEffect(key1 = Unit) {
        currentUser = ProfileSettingRepository.getUserByIdSync(auth.currentUser?.uid!!)!!
    }
    val text by viewModel.text.observeAsState("Settings")
    val nestedNavController = rememberNavController()

    NavHost(
        navController = nestedNavController,
        startDestination = "settings"
    ) {
        composable("settings") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(21.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = text,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    ProfileSection(currentUser) { newImageUrl ->
                        currentUser.profileImage = newImageUrl
                        currentUser.updateInfo(mapOf("profileImage" to newImageUrl))
                    }

                    // Settings Options
                    SettingsOption(
                        title = "Profile",
                        icon = Icons.Default.Person
                    ) {
                        nestedNavController.navigate("settings/profile")
                    }
                    HorizontalDivider()
                    SettingsOption(
                        title = "Appearance",
                        icon = Icons.Default.Image
                    ) {
                        nestedNavController.navigate("settings/appearance")
                    }
                    HorizontalDivider()
                    SettingsOption(
                        title = "Update Password",
                        icon = Icons.Default.VisibilityOff
                    ) {
                        nestedNavController.navigate("settings/update_password")
                    }
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(40.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                AccountAuthUtil.signOut(auth)
                                val activity = context as? Activity
                                activity?.let {
                                    it.finish()
                                    it.startActivity(it.intent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            border = BorderStroke(1.dp, Color(0xFFED3241)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFED3241)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Logout Icon",
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Logout",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }


        composable(
            route = "settings/profile"
        ) { _ ->
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(auth, nestedNavController, profileViewModel)
        }
        composable("settings/appearance") {
            val appearanceViewModel: AppearanceViewModel =
                viewModel(factory = AppearanceViewModelFactory(nestedNavController.context))
            AppearanceScreen(nestedNavController, appearanceViewModel)
        }
        composable("settings/update_password") {
            val updatePasswordViewModel: UpdatePasswordViewModel = viewModel()
            UpdatePasswordScreen(nestedNavController, updatePasswordViewModel)
        }
    }
}

@Composable
fun ProfileSection(currentUser: User, onProfileImageChange: (String) -> Unit) {
    val context = LocalContext.current
    var isUploading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            isUploading = true
            uploadImageToS3(
                context,
                it,
                onSuccess = { uploadedImageUrl ->
                    isUploading = false
                    onProfileImageChange(uploadedImageUrl)
                    UserRepository.updateProfileImage(currentUser.userId, uploadedImageUrl)
                },
                onFailure = { errorMessage ->
                    isUploading = false
                    Toast.makeText(
                        context,
                        "Image upload failed: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clickable {
                    imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = currentUser.profileImage?.takeIf { it.isNotBlank() }
                        ?: "https://thispersondoesnotexist.com/"
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = 12.dp, y = 12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF006FFD))
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = currentUser.fullName ?: "N/A",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = currentUser.email ?: "N/A",
            fontSize = 14.sp,
            color = Color(0xFF71727A)
        )
    }
}

@Composable
fun SettingsOption(title: String, icon: ImageVector, onClick: () -> Unit) {
    val iconColor = Color(0xFFC5C6CC)
    val dividerColor = Color(0xFFD4D6DD)
    val chevronColor = Color(0xFF8F9098)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(26.dp),
                tint = iconColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                modifier = Modifier.size(26.dp),
                tint = chevronColor
            )
        }
        HorizontalDivider(color = dividerColor, thickness = Dp.Hairline)
    }
}

package com.it2161.movieviewer.ui.screens.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.it2161.movieviewer.data.local.entities.UserEntity
import com.it2161.movieviewer.ui.components.CameraCapture
import com.it2161.movieviewer.ui.components.ErrorMessage
import com.it2161.movieviewer.ui.components.LoadingIndicator
import com.it2161.movieviewer.ui.viewmodels.ProfileState
import com.it2161.movieviewer.ui.viewmodels.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    userId: String,
    onLogout: () -> Unit
) {
    val profileState by viewModel.profileState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    
    var isEditMode by remember { mutableStateOf(false) }
    var editedPreferredName by remember { mutableStateOf("") }
    var editedDateOfBirth by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    LaunchedEffect(profileState) {
        if (profileState is ProfileState.Success) {
            val user = (profileState as ProfileState.Success).user
            editedPreferredName = user.preferredName
            editedDateOfBirth = user.dateOfBirth
            
            profileBitmap = user.profilePicturePath?.let { path ->
                try {
                    BitmapFactory.decodeFile(path)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    LaunchedEffect(updateState) {
        updateState?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearUpdateState()
            isEditMode = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    if (profileState is ProfileState.Success) {
                        IconButton(
                            onClick = {
                                if (isEditMode) {
                                    val user = (profileState as ProfileState.Success).user
                                    val profilePath = profileBitmap?.let { bitmap ->
                                        val file = File(context.filesDir, "profile_${userId}.jpg")
                                        FileOutputStream(file).use { out ->
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                        }
                                        file.absolutePath
                                    }
                                    
                                    val updatedUser = user.copy(
                                        preferredName = editedPreferredName,
                                        dateOfBirth = editedDateOfBirth,
                                        profilePicturePath = profilePath
                                    )
                                    viewModel.updateProfile(updatedUser)
                                } else {
                                    isEditMode = true
                                }
                            }
                        ) {
                            Text(
                                text = if (isEditMode) "SAVE" else "EDIT",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = profileState) {
                is ProfileState.Loading -> {
                    LoadingIndicator()
                }
                is ProfileState.Success -> {
                    val user = state.user
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isEditMode) {
                            CameraCapture(
                                currentImage = profileBitmap,
                                onImageCaptured = { profileBitmap = it }
                            )
                        } else {
                            if (profileBitmap != null) {
                                Image(
                                    bitmap = profileBitmap!!.asImageBitmap(),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Default Profile",
                                    modifier = Modifier.size(120.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = user.userId,
                            onValueChange = {},
                            label = { Text("User ID") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = if (isEditMode) editedPreferredName else user.preferredName,
                            onValueChange = { editedPreferredName = it },
                            label = { Text("Preferred Name") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditMode,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = if (isEditMode) editedDateOfBirth else user.dateOfBirth,
                            onValueChange = {},
                            label = { Text("Date of Birth") },
                            leadingIcon = {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditMode,
                            readOnly = true,
                            trailingIcon = {
                                if (isEditMode) {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        if (isEditMode) {
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        isEditMode = false
                                        editedPreferredName = user.preferredName
                                        editedDateOfBirth = user.dateOfBirth
                                        profileBitmap = user.profilePicturePath?.let { path ->
                                            try {
                                                BitmapFactory.decodeFile(path)
                                            } catch (e: Exception) {
                                                null
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel")
                                }
                                
                                Button(
                                    onClick = {
                                        val profilePath = profileBitmap?.let { bitmap ->
                                            val file = File(context.filesDir, "profile_${userId}.jpg")
                                            FileOutputStream(file).use { out ->
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                            }
                                            file.absolutePath
                                        }
                                        
                                        val updatedUser = user.copy(
                                            preferredName = editedPreferredName,
                                            dateOfBirth = editedDateOfBirth,
                                            profilePicturePath = profilePath
                                        )
                                        viewModel.updateProfile(updatedUser)
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = editedPreferredName.isNotBlank() && editedDateOfBirth.isNotBlank()
                                ) {
                                    Text("Save")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                is ProfileState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.loadProfile(userId) }
                    )
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                editedDateOfBirth = sdf.format(Date(millis))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

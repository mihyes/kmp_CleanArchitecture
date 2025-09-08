package com.example.demokmpapp

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import presentation.AndroidUserViewModel
import presentation.AndroidUserViewModelFactory
import kotlin.properties.ReadOnlyProperty
import domain.model.User

class MainActivity : ComponentActivity() {

    private val viewModel: AndroidUserViewModel by viewModels {
        val app = application as AndroidApplication
        AndroidUserViewModelFactory(app.diContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
//            App()
//        }
            MaterialTheme {
                UserScreen(viewModel = viewModel)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(viewModel: AndroidUserViewModel) {
    val context = LocalContext.current
    val app = context.applicationContext as AndroidApplication

//    val viewModel: AndroidUserViewModel by viewModels {
//        AndroidUserViewModelFactory(app.diContainer)
//    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var nameInput by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hello, Android Mekmp",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Name 입력 섹션
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter Name",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Enter Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        viewModel.saveName(nameInput)
                        nameInput = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = nameInput.isNotBlank()
                ) {
                    Text("Save to DB")
                }
            }
        }

        // 🔄 Load 버튼
        Button(
            onClick = { viewModel.loadAllData() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Load All Data")
        }


        // 📢 상태 메시지
        StatusCard(
            message = uiState.error ?: "succes",
            isError = uiState.error != null,
            isLoading = uiState.isLoading,
            onClearError = { viewModel.clearError() }
        )

        UserListCard(
            users = uiState.users,
            isLoading = uiState.isLoading,
            isLocalUser = { user -> viewModel.isLocalUser(user) },
            onDeleteUser = { user ->
                if (viewModel.isLocalUser(user)) {
                    //삭제 기능 구현
                }
            }
        )
    }
}


@Composable
fun StatusCard(
    message: String,
    isError: Boolean,
    isLoading: Boolean,
    onClearError: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isError)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = if(isError)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.onPrimaryContainer

            )

            if (isError) {
                TextButton(onClick = onClearError) {
                    Text("Clear")
                }
            }
        }
    }
}


@Composable
fun UserListCard(
    users: List<User>,
    isLoading: Boolean,
    isLocalUser: (User) -> Boolean,
    onDeleteUser: (User) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved User",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Badge {
                    Text("${users.size}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (isLoading) {
                LoadingState()
            } else if (users.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(users) { user ->
                        UserItemCard(
                            user = user,
                            isLocal = isLocalUser(user),
                            onDelete = { onDeleteUser(user) }
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun UserItemCard(
    user: User,
    isLocal: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isLocal) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                               text = "Local",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }

                Text(
                    text = user.platform,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "ID: ${user.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Empty",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No users saved yet",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Add your first user to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
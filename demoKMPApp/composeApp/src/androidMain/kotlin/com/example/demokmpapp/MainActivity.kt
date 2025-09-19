package com.example.demokmpapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import domain.model.User
import presentation.AOSUserPresenter

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    private val viewModel: AndroidUserViewModel by viewModels {
//        val app = application as AndroidApplication
//        AndroidUserViewModelFactory(app.diContainer)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            ->
            MaterialTheme {
                UserScreen()
            }

//            Text("Hello World")
        }
    }
}



//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}


@Composable
fun UserScreen(
    presenter: AOSUserPresenter = hiltViewModel()
//    viewModel: AndroidUserViewModel
) {

    val user by presenter.users.collectAsState() // 앱 라이프사이클에 따라 데이터 수집
    val uiState by presenter.uiState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { presenter.clearError() }) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = "Clear",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { presenter.refreshUser()},
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                   CircularProgressIndicator(
                       modifier = Modifier.size(16.dp),
                       color = Color.White
                   )
                } else {
                    Text("새로고침")
                }
            }

            Button(
                onClick = { showCreateDialog = true }
            ) {
                Text("사용자 추가")
            }
        }


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(user, key = { it.id }) { user ->
                UserItem(
                    user = user,
                    onEdit = { /* 편집 로직 */ },
                    onDelete = { presenter.deleteUser(user.id) }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onCreateRemote = { name, platform ->
                presenter.createUser(name, platform)
                showCreateDialog = false
            },
            onCreateLocal = { name, platform ->
                presenter.createUserLocale(name, platform)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun UserItem(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Platform: ${user.platform}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(
                    text = "Version: ${user.version}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Text(
                    text = if (user.id < 0) "로컬 사용자" else "서버 사용자",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (user.id < 0) Color.Blue else Color.Green
                )
            }

            Row{
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit User"
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete User",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

data class UserUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@Composable
fun CreateUserDialog(
    onDismiss: () -> Unit,
    onCreateRemote: (String, String) -> Unit,
    onCreateLocal: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("Android") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create User") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("User name") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = platform,
                    onValueChange = { platform = it },
                    label = { Text("Platform") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Row {
               TextButton(
                   onClick = {
                       if (name.isNotBlank() && platform.isNotBlank()) {
                           onCreateLocal(name, platform)
                       }
                   }
               ) {
                   Text("로컬에만 저장")
               }

                TextButton(

                    onClick = {
                        if (name.isNotBlank() && platform.isNotBlank()) {
                            onCreateRemote(name, platform)
                        }
                    }
                ) {
                    Text("서버에 저장")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }

    )

}
package com.example.a1155229161_assignment2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
// 确保 Bot 数据类的定义如下：
class BotActivity : ComponentActivity() {
    companion object {
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_EMAIL = "extra_email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 添加日志
        //Log.d("BotActivity", "Intent extras: ${intent.extras}")
        val username = intent.getStringExtra(EXTRA_USERNAME) ?: ""
        //Log.d("BotActivity", "Username: $username")
        val email = intent.getStringExtra(EXTRA_EMAIL)
        //Log.d("BotActivity", "Email: $email")

        setContent {
            MaterialTheme {
                BotScreen(username = username)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotScreen(
    username: String,
    viewModel: BotViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var botName by remember { mutableStateOf("") }
    var prompt by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var user_name = username
    //Log.d("BotActivity", "User_name: $user_name")
    val uiState by viewModel.uiState.collectAsState()
    val bots by viewModel.bots.collectAsState(initial = emptyList())

    // 启动时获取机器人列表
    LaunchedEffect(username) {
        viewModel.fetchUserBots(username)
    }

    // 处理 UI 状态
    LaunchedEffect(uiState) {
        when (uiState) {
            is BotUiState.Success -> {
                showDialog = false
                viewModel.fetchUserBots(username)
            }
            is BotUiState.Error -> {
                errorMessage = (uiState as BotUiState.Error).message
                showError = true
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat to your Jarvis") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 用户头像
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.firstOrNull()?.uppercase() ?: "U",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // 欢迎文本
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = username,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 显示机器人列表
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(bots,
                    key = {bot -> bot.id }) { bot ->
                    BotCard(bot = bot)
                }
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Create New Bot")
            }
        }

        // 创建机器人对话框
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Create New Bot") },
                text = {
                    Column {
                        TextField(
                            value = botName,
                            onValueChange = { botName = it },
                            label = { Text("Bot Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        TextField(
                            value = prompt,
                            onValueChange = { prompt = it },
                            label = { Text("Prompt") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            minLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (botName.isNotBlank() && prompt.isNotBlank()) {
                                viewModel.createChatbot(user_name, botName, prompt)
                            } else {
                                errorMessage = "Please fill in all fields"
                                showError = true
                            }
                        }
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // 错误提示
        if (showError) {
            AlertDialog(
                onDismissRequest = { showError = false },
                title = { Text("Error") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(onClick = { showError = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun BotCard(bot: Bot) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { /* 处理点击事件 */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = bot.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            bot.lastMessage?.let { lastMessage ->
                Text(
                    text = lastMessage + "...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

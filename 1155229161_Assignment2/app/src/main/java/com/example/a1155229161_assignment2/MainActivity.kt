package com.example.a1155229161_assignment2
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val loginViewModel: LoginViewModel = viewModel()
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = { username, email ->
                        // 启动 BotActivity
                        val intent = Intent(this, BotActivity::class.java).apply {
                            putExtra(BotActivity.EXTRA_USERNAME, username)
                            putExtra(BotActivity.EXTRA_EMAIL, email)
                        }
                        startActivity(intent)
                        finish() // 关闭登录界面
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (username:String, email: String?) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    // UI State
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                val successState = uiState as LoginUiState.Success
                dialogMessage = (uiState as LoginUiState.Success).message
                showDialog = true
                onLoginSuccess(successState.username, successState.email)
            }
            is LoginUiState.Error -> {
                dialogMessage = (uiState as LoginUiState.Error).message
                showDialog = true
            }
            else -> {}
        }
    }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var registerEmail by remember { mutableStateOf("") }
    var registerUsername by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    // Loading 状态指示器
    if (uiState is LoginUiState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }



    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (uiState is LoginUiState.Success) "Success" else "Error") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // 注册对话框
    if (showRegisterDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = { Text("Register") },
            text = {
                Column {
                    OutlinedTextField(
                        value = registerUsername,
                        onValueChange = { registerUsername = it },
                        label = { Text("Username") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = registerPassword,
                        onValueChange = { registerPassword = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = registerEmail,
                        onValueChange = { registerEmail = it },
                        label = { Text("Email (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (registerUsername.isNotEmpty() && registerPassword.isNotEmpty()) {
                            viewModel.register(registerUsername, registerPassword, registerEmail)
                            showRegisterDialog = false
                            // 清空注册表单
                            registerUsername = ""
                            registerPassword = ""
                            registerEmail = ""
                        }
                    }
                ) {
                    Text("Register")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    //
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(top = 32.dp)
        )

        // Slogan
        Text(
            text = "Build your own chat model!",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
        )
        // Username TextField with error
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = ""
            },
            label = { Text("Username") },
            isError = usernameError.isNotEmpty(),
            supportingText = {
                if (usernameError.isNotEmpty()) {
                    Text(
                        text = usernameError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        )

        // Password TextField with error
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = ""
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError.isNotEmpty(),
            supportingText = {
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        )

        // Buttons with validation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    var hasError = false
                    if (username.isEmpty()) {
                        usernameError = "Username cannot be empty"
                        hasError = true
                    }
                    if (password.isEmpty()) {
                        passwordError = "Password cannot be empty"
                        hasError = true
                    }
                    if (!hasError) {
                        viewModel.login(username, password)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Login")
            }

            Button(
                onClick = { showRegisterDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Register")
            }
        }
    }
}


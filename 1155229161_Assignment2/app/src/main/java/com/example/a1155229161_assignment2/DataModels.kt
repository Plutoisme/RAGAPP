package com.example.a1155229161_assignment2

// UserData
data class UserLogin(
    val username: String,
    val password: String
)

data class UserRegister(
    val username: String,
    val password: String,
    val email: String? = null
)

data class UserResponse(
    val username: String,
    val email: String?,
    val message: String
)

data class LoginResponse(
    val username: String,
    val email: String?,
    val message: String
)

// ModelData
data class ChatbotCreate(
    val username: String,
    val name: String,
    val prompt: String
)

data class ChatbotResponse(
    val id: String,
    val name: String,
    val prompt: String,
    val message: String
)
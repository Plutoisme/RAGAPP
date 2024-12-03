package com.example.a1155229161_assignment2

// NetworkClient.kt
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class BotListResponse(
    val status: String,
    val bots: List<BotResponse>
)

data class BotResponse(
    val id: String,
    val name: String,
    val prompt: String,
    val last_message: MessageResponse?
)

data class MessageResponse(
    val role: String,
    val content: String,
    val timestamp: String
)

object NetworkClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"  // 替换为你的服务器地址
    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun login(username: String, password: String): Result<UserResponse> {
        val userLogin = UserLogin(username, password)
        return makeRequest("login", userLogin)
    }

    suspend fun register(username: String, password: String, email: String? = null): Result<UserResponse> {
        val userRegister = UserRegister(username, password, email)
        return makeRequest("register", userRegister)
    }

    private suspend fun <T> makeRequest(endpoint: String, requestBody: T): Result<UserResponse> =
        suspendCoroutine { continuation ->
            val request = Request.Builder()
                .url("${BASE_URL}${endpoint}")
                .post(gson.toJson(requestBody).toRequestBody(JSON))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            val userResponse = gson.fromJson(responseBody, UserResponse::class.java)
                            continuation.resume(Result.success(userResponse))
                        } else {
                            continuation.resume(Result.failure(IOException("HTTP ${response.code}: ${response.body?.string()}")))
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }

    suspend fun createChatbot(username:String, name: String, prompt: String): Result<ChatbotResponse> {
        val chatbotCreate = ChatbotCreate(username, name, prompt)
        return suspendCoroutine { continuation ->
            val request = Request.Builder()
                .url("${BASE_URL}create_chatbot")
                .post(gson.toJson(chatbotCreate).toRequestBody(JSON))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            val chatbotResponse = gson.fromJson(responseBody, ChatbotResponse::class.java)
                            continuation.resume(Result.success(chatbotResponse))
                        } else {
                            continuation.resume(Result.failure(IOException("HTTP ${response.code}: ${response.body?.string()}")))
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }
    suspend fun getUserBots(username: String): Result<BotListResponse> {
        return suspendCoroutine { continuation ->
            val request = Request.Builder()
                .url("${BASE_URL}get_user_bots?username=$username")
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            val botListResponse = gson.fromJson(responseBody, BotListResponse::class.java)
                            continuation.resume(Result.success(botListResponse))
                        } else {
                            continuation.resume(Result.failure(IOException("HTTP ${response.code}: ${response.body?.string()}")))
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }
}
package com.example.demokmpapp

import data.remote.CreateUserRequest
import data.remote.UserDto
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

fun main() {
    println("🚀 Server starting on port $SERVER_PORT...")
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

// In-memory storage for users
private val users = ConcurrentHashMap<Long, UserDto>()
private val idCounter = AtomicLong(1)

// Initialize with sample data
private fun initSampleData() {
    if (users.isEmpty()) {
        listOf(
            UserDto(idCounter.getAndIncrement(), "Alice", "1.0.0", "Server"),
            UserDto(idCounter.getAndIncrement(), "Bob", "1.0.0", "Server"),
            UserDto(idCounter.getAndIncrement(), "Charlie", "1.0.0", "Server")
        ).forEach { users[it.id] = it }
        println("✅ Sample data initialized: ${users.size} users")
    }
}

fun Application.module() {
    install(CORS) {
        allowHost("localhost:5173") // Vite dev server
        allowHost("localhost:3000") // Other dev servers
        allowHost("127.0.0.1:5173")
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    initSampleData()

    routing {
        // GET / - 서버 상태 및 사용자 목록 HTML 페이지
        get("/") {
            val userList = users.values.sortedBy { it.id }
            val html = buildString {
                appendLine("<!DOCTYPE html>")
                appendLine("<html><head>")
                appendLine("<meta charset='UTF-8'>")
                appendLine("<title>KMP Demo Server</title>")
                appendLine("<style>")
                appendLine("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }")
                appendLine("h1 { color: #333; }")
                appendLine("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
                appendLine("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }")
                appendLine("th { background-color: #4CAF50; color: white; }")
                appendLine("tr:nth-child(even) { background-color: #f2f2f2; }")
                appendLine("tr:hover { background-color: #ddd; }")
                appendLine(".status { color: #4CAF50; font-weight: bold; }")
                appendLine(".count { background: #e3f2fd; padding: 10px 20px; border-radius: 8px; display: inline-block; margin: 10px 0; }")
                appendLine("</style>")
                appendLine("</head><body>")
                appendLine("<h1>🚀 KMP Demo Server</h1>")
                appendLine("<p class='status'>Server is running on port $SERVER_PORT</p>")
                appendLine("<div class='count'>Total Users: <strong>${userList.size}</strong></div>")
                appendLine("<h2>👥 User List</h2>")
                appendLine("<table>")
                appendLine("<tr><th>ID</th><th>Name</th><th>Platform</th><th>Version</th></tr>")
                userList.forEach { user ->
                    appendLine("<tr><td>${user.id}</td><td>${user.name}</td><td>${user.platform}</td><td>${user.version}</td></tr>")
                }
                appendLine("</table>")
                appendLine("<h2>📡 API Endpoints</h2>")
                appendLine("<ul>")
                appendLine("<li><code>GET /users</code> - 모든 사용자 조회</li>")
                appendLine("<li><code>GET /users/{id}</code> - 특정 사용자 조회</li>")
                appendLine("<li><code>POST /users</code> - 새 사용자 생성</li>")
                appendLine("<li><code>PUT /users/{id}</code> - 사용자 수정</li>")
                appendLine("<li><code>DELETE /users/{id}</code> - 사용자 삭제</li>")
                appendLine("</ul>")
                appendLine("<p><small>Last updated: ${java.time.LocalDateTime.now()}</small></p>")
                appendLine("</body></html>")
            }
            call.respondText(html, ContentType.Text.Html)
        }

        // GET /users - 모든 사용자 조회
        get("/users") {
            println("📥 GET /users - Returning ${users.size} users")
            call.respond(users.values.toList())
        }

        // GET /users/{id} - 특정 사용자 조회
        get("/users/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            println("📥 GET /users/$id")
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                return@get
            }
            val user = users[id]
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                return@get
            }
            call.respond(user)
        }

        // POST /users - 새 사용자 생성
        post("/users") {
            try {
                val request = call.receive<CreateUserRequest>()
                println("📥 POST /users - Creating user: ${request.name} (${request.platform})")
                val newUser = UserDto(
                    id = idCounter.getAndIncrement(),
                    name = request.name,
                    version = "1.0.0",
                    platform = request.platform
                )
                users[newUser.id] = newUser
                println("✅ User created: $newUser")
                call.respond(HttpStatusCode.Created, newUser)
            } catch (e: Exception) {
                println("❌ Error creating user: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Unknown error")))
            }
        }

        // PUT /users/{id} - 사용자 수정
        put("/users/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            println("📥 PUT /users/$id")
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                return@put
            }
            if (!users.containsKey(id)) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                return@put
            }
            try {
                val request = call.receive<CreateUserRequest>()
                val updatedUser = UserDto(
                    id = id,
                    name = request.name,
                    version = "1.0.0",
                    platform = request.platform
                )
                users[id] = updatedUser
                println("✅ User updated: $updatedUser")
                call.respond(updatedUser)
            } catch (e: Exception) {
                println("❌ Error updating user: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Unknown error")))
            }
        }

        // DELETE /users/{id} - 사용자 삭제
        delete("/users/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            println("📥 DELETE /users/$id")
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                return@delete
            }
            if (users.remove(id) == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                return@delete
            }
            println("✅ User $id deleted")
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
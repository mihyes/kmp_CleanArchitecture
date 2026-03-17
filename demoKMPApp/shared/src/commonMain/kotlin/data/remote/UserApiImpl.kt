package data.remote


import com.example.demokmpapp.BASE_URL
import data.remote.CreateUserRequest
import data.remote.UserDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

// HttpClient 사용하여 API 통신하는 부분
class UserApiImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = BASE_URL
) : UserApi {

    override suspend fun getUsers(): List<UserDto> {
        return httpClient.get("$baseUrl/users").body()
    }

    override suspend fun getUserById(id: Long): UserDto {
        return httpClient.get("$baseUrl/users/$id").body()
    }

    override suspend fun createUser(request: CreateUserRequest): UserDto {
        return httpClient.post("$baseUrl/users") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun updateUser(id: Long, request: CreateUserRequest): UserDto {
        return httpClient.put("$baseUrl/users/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteUser(id: Long) {
        httpClient.delete("$baseUrl/users/$id")
    }
}
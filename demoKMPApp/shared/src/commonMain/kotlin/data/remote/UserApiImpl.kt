package data.remote


import data.remote.CreateUserRequest
import data.remote.UserDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*


class UserApiImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://api.example.com"
) : UserApi {

    override suspend fun getUsers(): List<UserDto> {
        return httpClient.get("$baseUrl/users").body()
    }

    override suspend fun getUserById(id: Long): UserDto {
        return httpClient.get("$baseUrl/id/$id").body()
    }

    override suspend fun getUserByName(name: String): UserDto {
        return httpClient.get("$baseUrl/name/$name").body()
    }

    override suspend fun createUser(request: CreateUserRequest): UserDto {
        return httpClient.post("$baseUrl/users") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun updateUser(id: Long, request: CreateUserRequest): UserDto {
        return httpClient.put("$baseUrl/id/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteUser(id: Long) {
        httpClient.delete("$baseUrl/id/$id")
    }
}
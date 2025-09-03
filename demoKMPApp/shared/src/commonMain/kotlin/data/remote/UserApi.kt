package data.remote

import data.remote.UserDto
import data.remote.CreateUserRequest

interface UserApi {
    suspend fun getUsers(): List<UserDto>
    suspend fun getUserById(id: Long): UserDto
    suspend fun getUserByName(name: String): UserDto
    suspend fun createUser(request: CreateUserRequest): UserDto
    suspend fun updateUser(id: Long, request: CreateUserRequest): UserDto
    suspend fun deleteUser(id: Long)
}

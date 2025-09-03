package data.remote

import data.local.DataEntity
import kotlinx.serialization.Serializable


@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val version: String,
    val platform: String
)


// 예시
@Serializable
data class CreateUserRequest(
    val name: String,
    val platform: String
)


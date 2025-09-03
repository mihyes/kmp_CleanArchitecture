package domain.model


data class User(
    val id: Long,
    val name: String,
    val version: String,
    val platform: String
)
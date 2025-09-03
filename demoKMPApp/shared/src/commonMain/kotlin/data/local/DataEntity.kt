package data.local

import com.example.demokmpapp.Platform
import io.ktor.http.content.Version
import data.remote.UserApi
import data.remote.UserApiImpl
import data.repository.UserRepositoryImpl
import domain.repository.UserRepository
import domain.usecase.CreateUserUseCase
import domain.usecase.GetUserUseCase
import domain.usecase.RefreshUserUseCase
import presentation.UserPresenter

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import kotlinx.serialization.json.Json


data class DataEntity (
    val id: Long,
    val name: String,
    val version: String,
    val platform: String
)


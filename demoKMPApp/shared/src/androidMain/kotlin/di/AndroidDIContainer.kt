package di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.demokmpapp.database.Database
import data.UserDaoImpl
import data.local.UserDao
import data.remote.UserApi
import data.remote.UserApiImpl
import data.repository.UserRepositoryImpl
import di.AndroidSqlDriverFactory
import domain.repository.UserRepository
import domain.usecase.ClearLocalUsersUseCase
import domain.usecase.CreateUserLocallyUseCase
import domain.usecase.CreateUserUseCase
import domain.usecase.DeleteUserLocallyUseCase
import domain.usecase.GetUserUseCase
import domain.usecase.RefreshUserUseCase
import domain.usecase.UpdateUserLocallyUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import presentation.UserPresenter


class AndroidDIContainer private constructor(
    private val context: Context
) {

    // ===========================================
    // 📊 DATABASE LAYER
    // ===========================================
    private val sqlDriver: SqlDriver by lazy {
        AndroidSqliteDriver(Database.Schema, context, "user.db")
    }

    private val database: Database by lazy {
        Database(sqlDriver)
    }

    private val userDao: UserDao by lazy {
        UserDaoImpl(database)
    }


    // ===========================================
    // 📦 REPOSITORY LAYER
    // ===========================================
    private val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }


    private val userApi: UserApi by lazy {
        UserApiImpl(httpClient)
    }

    private val userRepository: UserRepository by lazy {
        UserRepositoryImpl(userApi, userDao)
    }


    // ===========================================
    // 🎭 DOMAIN LAYER (USE CASES)
    // ===========================================
    private val getUsersUseCase: GetUserUseCase by lazy {
        GetUserUseCase(userRepository)
    }

    private val createUserUseCase: CreateUserUseCase by lazy {
        CreateUserUseCase(userRepository)
    }

    private val refreshUsersUseCase: RefreshUserUseCase by lazy {
        RefreshUserUseCase(userRepository)
    }


    private val createUserLocallyUseCase: CreateUserLocallyUseCase by lazy {
        CreateUserLocallyUseCase(userRepository)
    }

    private val updateUserLocallyUseCase: UpdateUserLocallyUseCase by lazy {
        UpdateUserLocallyUseCase(userRepository)
    }

    private val deleteUserLocallyUseCase: DeleteUserLocallyUseCase by lazy {
        DeleteUserLocallyUseCase(userRepository)
    }

    private val clearLocalUsersUseCase: ClearLocalUsersUseCase by lazy {
        ClearLocalUsersUseCase(userRepository)
    }


    // ===========================================
    // 🎨 PRESENTATION LAYER
    // ===========================================

    private val presentationScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob())
    }

    fun getUserPresenter(): UserPresenter {
        return UserPresenter(
            getUsersUseCase,
            createUserUseCase = createUserUseCase,
            refreshUserUseCase = refreshUsersUseCase,
            createUserLocallyUseCase = createUserLocallyUseCase,
            updateUserLocallyUseCase = updateUserLocallyUseCase,
            deleteUserLocallyUseCase = deleteUserLocallyUseCase,
            clearLocalUsersUseCase = clearLocalUsersUseCase,
            coroutineScope = presentationScope
        )
    }


//    fun getUserRepository(): UserRepository = userRepository

    companion object {
        @Volatile
        private var INSTANCE: AndroidDIContainer? = null
        fun getInstance(context: Context): AndroidDIContainer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AndroidDIContainer(context.applicationContext).also { INSTANCE = it }
            }
        }

        fun clearInstance() {
            INSTANCE = null
        }
    }

}
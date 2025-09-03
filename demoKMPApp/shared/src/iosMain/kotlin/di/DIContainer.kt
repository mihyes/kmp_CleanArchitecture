package di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import data.local.UserDao
import data.UserDaoImpl
import data.remote.UserApi
import data.remote.UserApiImpl
import data.repository.UserRepositoryImpl
import com.example.demokmpapp.database.Database
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import domain.repository.UserRepository
import domain.usecase.RefreshUserUseCase
import domain.usecase.CreateUserUseCase
import domain.usecase.GetUserUseCase
import domain.usecase.ClearLocalUsersUseCase
import domain.usecase.CreateUserLocallyUseCase
import domain.usecase.UpdateUserLocallyUseCase
import domain.usecase.DeleteUserLocallyUseCase
import presentation.UserPresenter

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import presentation.IOSUserPresenter
import kotlin.jvm.JvmStatic

class DIContainer {

    /**
     * SQLite 드라이버 - iOS 네이티브 드라이버 사용
     * lazy: 실제 사용될 때까지 생성하지 않음 (메모리 절약)
     */
    private val sqlDriver: SqlDriver by lazy {
        NativeSqliteDriver(Database.Schema, "user.db")
    }



    /**
     * SQLDelight Database 인스턴스
     * 앱 전체에서 하나만 존재 (Singleton)
     */
    private val database: Database by lazy {
        Database(sqlDriver)
    }


    /**
     * User 테이블 접근을 위한 DAO
     * 데이터베이스 CRUD 작업 담당
     */
    private val userDao: UserDao by lazy {
        UserDaoImpl(database)
    }


    /**
     * HTTP 클라이언트 설정
     * - ContentNegotiation: JSON 직렬화/역직렬화
     * - Logging: 네트워크 요청/응답 로깅
     */
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


    /**
     * User API 구현체
     * REST API 통신 담당
     */
    private val userApi: UserApi by lazy {
        UserApiImpl(httpClient)
    }



    /**
     * UserRepository 구현체
     * 로컬(SQLDelight) + 원격(API) 데이터 소스를 통합 관리
     * - 캐싱 전략: 로컬 DB를 캐시로 사용
     * - 오프라인 지원: 네트워크 없어도 로컬 데이터 사용
     */
    private val userRepository: UserRepository by lazy {
        UserRepositoryImpl(userApi, userDao)
    }




    /**
     * Use Cases - 각각은 하나의 비즈니스 기능을 담당
     */
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


    /**
     * 코루틴 스코프 - Presenter에서 비동기 작업용
     * SupervisorJob: 하나의 자식이 실패해도 다른 자식들에 영향 없음
     */
    private val presentationScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob())
    }




    /**
     * UserPresenter 제공
     * iOS ViewModel에서 이것을 사용
     */

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


    /**
     * iOS용 특별한 Presenter (Flow → Callback 변환)
     */
    fun getIOSUserPresenter(): IOSUserPresenter {
        return IOSUserPresenter(getUserPresenter())
    }


    /**
     * 테스트용 - Repository 직접 접근
     */
    fun getUserRepository(): UserRepository = userRepository

    /**
     * 디버깅용 - 데이터베이스 직접 접근
     */
    fun getDatabase(): Database = database


    /**
     * 리소스 정리 (앱 종료 시 호출)
     */
    fun cleanup() {
        httpClient.close()
        sqlDriver.close()
        // 필요시 다른 리소스들도 정리
    }


    companion object {
        /**
         * 앱 전체에서 하나의 인스턴스만 사용
         * Thread-Safe Singleton
         */
        val shared: DIContainer by lazy { DIContainer() }

        /**
         * 테스트용 - Mock DIContainer 주입 가능
         */
        fun getInstance(): DIContainer {
            return shared
        }

        var testInstance: DIContainer? = null
            set(value) {
                field = value
            }


        fun getTestInstance(): DIContainer {
            return testInstance ?: shared
        }
    }
}
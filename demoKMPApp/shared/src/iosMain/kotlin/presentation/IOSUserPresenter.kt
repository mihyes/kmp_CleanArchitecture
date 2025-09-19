package presentation


import domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine





class IOSUserPresenter(
    val presenter: UserPresenter
) {

    private val iosScope = CoroutineScope(SupervisorJob())


    /**
     * StateFlow를 Swift callback으로 변환
     * @param callback: Swift에서 제공할 콜백 함수
     */




    /**
     * Swift async/await와 호환되는 사용자 목록 가져오기
     */
    suspend fun getUsers(): List<User> = suspendCoroutine { continuation ->
        val currentUsers = presenter.uiState.value.users
        // 데이터 복사로 thread-safety 보장
        val usersCopy = currentUsers.map { user ->
            User(
                id = user.id,
                name = user.name,
                platform = user.platform,
                version = user.version
            )
        }
        continuation.resume(usersCopy)
    }

    /**
     * 현재 상태를 한 번만 가져오기 (Swift async/await 호환)
     */
    suspend fun getCurrentState(): UserUiState = suspendCoroutine { continuation ->
        val currentState = presenter.uiState.value
        // 상태 복사로 안전성 보장
        val stateCopy = UserUiState(
            users = currentState.users.map { user ->
                User(
                    id = user.id,
                    name = user.name,
                    platform = user.platform,
                    version = user.version
                )
            },
            isLoading = currentState.isLoading,
            error = currentState.error
        )
        continuation.resume(stateCopy)
    }




    fun observeUiState(callback: (UserUiState) -> Unit) {
        iosScope.launch {
            presenter.uiState.collect { state ->
                callback(state)
            }
        }
    }

    // 기본 기능들을 그대로 전달
    fun refreshUsers() = presenter.refreshUsers()
    fun createUser(
        name: String,
        platform: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        iosScope.launch {
            try {
                presenter.createUser(name, platform)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to create user")
            }
        }
    }


    fun clearError() = presenter.clearError()



    /**
     * 로컬 사용자 생성 (Swift async/await 호환)
     */
    suspend fun createUserLocallyAsync(name: String, platform: String) = suspendCoroutine<Unit> { continuation ->
        iosScope.launch {
            try {
                presenter.createUserLocally(name, platform)
                continuation.resume(Unit)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    fun createUserLocally(name: String, platform: String,
                          onSuccess: () -> Unit = {},
                          onError: (String) -> Unit = {}) {
        iosScope.launch {
            try {
                presenter.createUserLocally(name, platform)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to create user")
            }
        }
    }

    fun updateUserLocally(id: Long, name: String, platform: String,
                          onSuccess: () -> Unit = {},
                          onError: (String) -> Unit = {}) {
        iosScope.launch {
            try {
                presenter.updateUserLocally(id, name, platform)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to create user")
            }
        }
    }


    /**
     * 로컬 사용자 삭제 (Swift async/await 호환)
     */
    suspend fun deleteUserLocallyAsync(id: Long) = suspendCoroutine<Unit> { continuation ->
        iosScope.launch {
            try {
                presenter.deleteUserLocally(id)
                continuation.resume(Unit)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    fun deleteUserLocally(id: Long,
                          onSuccess: () -> Unit = {},
                          onError: (String) -> Unit = {}) {
        iosScope.launch {
            try {
                presenter.deleteUserLocally(id)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to create user")
            }
        }
    }




    /**
     * 로컬 데이터베이스 초기화 (Swift async/await 호환)
     */
    suspend fun clearDatabaseAsync() = suspendCoroutine<Unit> { continuation ->
        iosScope.launch {
            try {
                presenter.clearLocalUsers()
                continuation.resume(Unit)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    fun clearUserLocally(onSuccess: () -> Unit = {},
                   onError: (String) -> Unit = {}) {
        iosScope.launch {
            try {
                presenter.clearLocalUsers()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to create user")
            }
        }
    }




    // Swift에서 사용하기 쉬운 추가 메서드들
    /**
    * 현재 사용자 목록 (동기적 접근)
    */
    fun getCurrentUsers(): List<User> {
        return presenter.uiState.value.users.map { user ->
            User(
                id = user.id,
                name = user.name,
                platform = user.platform,
                version = user.version
            )
        }
    }


    /**
     * 현재 로딩 상태
     */
    fun isCurrentlyLoading(): Boolean = presenter.uiState.value.isLoading

    /**
     * 현재 에러 상태
     */
    fun getCurrentError(): String? = presenter.uiState.value.error


    /**
     * 리소스 정리
     */
    fun cleanup() {
        iosScope.cancel()
    }

}

/**
 * Flow를 callback으로 변환하는 확장 함수
 */
fun <T> Flow<T>.collectAsCallback(
    coroutineScope: CoroutineScope,
    callback: (T) -> Unit
) {
    coroutineScope.launch {
        collect { value ->
            callback(value)
        }
    }
}
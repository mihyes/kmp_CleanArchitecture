package presentation


import domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class IOSUserPresenter(
    private val presenter: UserPresenter
) {

    private val iosScope = CoroutineScope(SupervisorJob())


    /**
     * StateFlow를 Swift callback으로 변환
     * @param callback: Swift에서 제공할 콜백 함수
     */
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
    fun getCurrentUsers(): List<User> = presenter.uiState.value.users
    fun isCurrentlyLoading(): Boolean = presenter.uiState.value.isLoading
    fun getCurrentError(): String? = presenter.uiState.value.error

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
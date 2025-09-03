package presentation

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import domain.model.User
import domain.usecase.ClearLocalUsersUseCase
import domain.usecase.CreateUserLocallyUseCase
import domain.usecase.CreateUserUseCase
import domain.usecase.DeleteUserLocallyUseCase
import domain.usecase.GetUserUseCase
import domain.usecase.RefreshUserUseCase
import domain.usecase.UpdateUserLocallyUseCase
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UserUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


class UserPresenter(
    private val getUserUseCase: GetUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val refreshUserUseCase: RefreshUserUseCase,

    // 🆕 로컬 전용 Use Cases 추가
    private val createUserLocallyUseCase: CreateUserLocallyUseCase,
    private val updateUserLocallyUseCase: UpdateUserLocallyUseCase,
    private val deleteUserLocallyUseCase: DeleteUserLocallyUseCase,
    private val clearLocalUsersUseCase: ClearLocalUsersUseCase,
    private val coroutineScope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(UserUiState())
    @NativeCoroutinesState
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        observeUsers()
    }

    private fun observeUsers() {
        getUserUseCase()
            .onEach { users ->
                _uiState.value = _uiState.value.copy(
                    users = users,
                    isLoading = false
                )
            }
            .launchIn(coroutineScope)
    }

    fun refreshUsers() {
        coroutineScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            refreshUserUseCase()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    @NativeCoroutines
    suspend fun createUser(name: String, platform: String) {
        coroutineScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true,  error = null)
        }

        createUserUseCase(name, platform)
            .onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
    }



    // ===========================================
    // 🗄️ 새로운 로컬 전용 메서드들
    // ===========================================

    /**
     * 🆕 로컬 데이터베이스에만 사용자 생성
     */

    @NativeCoroutines
    suspend fun createUserLocally(name: String, platform: String) {
        coroutineScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        }

        createUserLocallyUseCase(name, platform)
            .onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }

    }

    @NativeCoroutines
   suspend fun updateUserLocally(id: Long, name: String, platform: String) {
        coroutineScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        }
        updateUserLocallyUseCase(id,name, platform)
            .onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }

    }

    @NativeCoroutines
    suspend fun deleteUserLocally(id: Long) {
        coroutineScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        }
        deleteUserLocallyUseCase(id)
            .onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }

    }


    @NativeCoroutines
    suspend fun clearLocalUsers() {
        coroutineScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        }

        clearLocalUsersUseCase()
            .onSuccess { users ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }

    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

}
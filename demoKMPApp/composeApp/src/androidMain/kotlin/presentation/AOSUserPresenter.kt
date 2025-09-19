package presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.model.User
import domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AOSUserPresenter @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    val users: StateFlow<List<User>> = userRepository.getUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun refreshUser() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
             userRepository.refreshUsers()
             _uiState.value = uiState.value.copy(isLoading = false)
            } catch (error: Exception) {
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    fun createUser(name: String, platform: String) {
        viewModelScope.launch {
            try {
                userRepository.createUser(name, platform)
            } catch (error: Exception) {
                _uiState.value = uiState.value.copy(
                    error = error.message ?: "Failed to create user"
                )
            }
        }
    }

    fun createUserLocale(name: String, platform: String) {
        viewModelScope.launch {
            try {
                userRepository.createUserLocally(name, platform)
            } catch (error: Exception) {
                _uiState.value = uiState.value.copy(
                    error = error.message ?: "Failed to create user Locally"
                )
            }
        }
    }



    fun updateUser(id: Long, name: String, platform: String) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(id, name, platform)
            } catch (error: Exception) {
                _uiState.value = uiState.value.copy(
                    error = error.message ?: "Failed to update user"
                )
            }
        }
    }

    fun updateUserLocale(id: Long, name: String, platform: String) {
        viewModelScope.launch {
            try {
                userRepository.updateUserLocally(id, name, platform)
            } catch (error: Exception) {
                _uiState.value = uiState.value.copy(
                    error = error.message ?: "Failed to update user Locally"
                )
            }
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(id)
            } catch (error: Exception) {
                _uiState.value = uiState.value.copy(
                    error = error.message ?: "Failed to delete user"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = uiState.value.copy(error = null)
    }





}
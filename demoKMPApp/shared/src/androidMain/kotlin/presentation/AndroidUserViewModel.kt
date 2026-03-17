//package presentation
//
//import android.content.Context
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import di.AndroidDIContainer
//import domain.model.User
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class AndroidUserViewModel(
//    private val presenter: UserPresenter
//): ViewModel() {
//
//    val uiState: StateFlow<UserUiState> = presenter.uiState
//
//
//    fun saveName(name: String) {
//        viewModelScope.launch(Dispatchers.Main) {
//            saveNameUseCase(name)
//        }
//    }
//    suspend fun saveNameUseCase(name: String) {
//        if (name.isBlank()) return
//        presenter.createUserLocally(name,"Android")
//    }
//
//    fun loadAllData() {
//        viewModelScope.launch {
//            val currentUsers = presenter.uiState.value.users
//            println("Current Users: $currentUsers")
//        }
//    }
//
//
//    fun refresh() {
//        presenter.refreshUsers()
//    }
//
//    suspend fun clearAllLoaclUsers() {
//        presenter.clearLocalUsers()
//    }
//
//    fun clearError() {
//        presenter.clearError()
//    }
//
//    fun isLocalUser(user: User): Boolean {
//        return user.id < 0
//    }
//
//
//    // ===========================================
//    // 📊 STATE ACCESS (iOS와 동일)
//    // ===========================================
//    val users: List<User>
//        get() = presenter.uiState.value.users
//
//    val isLoading: Boolean
//        get() = presenter.uiState.value.isLoading
//
//    val error: String?
//        get() = presenter.uiState.value.error
//
//
//    /**
//     * 로컬 사용자와 서버 사용자 분리 (iOS와 동일)
//     */
//    val localUsers: List<User>
//        get() = users.filter { it.id < 0 }
//
//    val serverUsers: List<User>
//        get() = users.filter { it.id > 0 }
//
//}
//
//class AndroidUserViewModelFactory(
//    private val diContainer: AndroidDIContainer
//): ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(AndroidUserViewModel::class.java)) {
//            return AndroidUserViewModel(diContainer.getUserPresenter()) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//
//}
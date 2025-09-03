package domain.repository

import com.example.demokmpapp.Platform
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import domain.model.User
import kotlinx.coroutines.flow.Flow


interface UserRepository {
    @NativeCoroutines
    fun getUser(): Flow<List<User>>

    @NativeCoroutines
    suspend fun getUserById(id: Long): User?

    @NativeCoroutines
    suspend fun refreshUsers()

    @NativeCoroutines
    suspend fun createUser(name: String, platform: String): User

    @NativeCoroutines
    suspend fun updateUser(id: Long, name: String, platform: String): User

    @NativeCoroutines
    suspend fun deleteUser(id: Long)


    @NativeCoroutines
    suspend fun createUserLocally(name: String, platform: String): User
    @NativeCoroutines
    suspend fun updateUserLocally(id:Long, name: String, platform: String): User
    @NativeCoroutines
    suspend fun deleteUserLocally(id: Long)
    @NativeCoroutines
    suspend fun clearUserLocally()
}



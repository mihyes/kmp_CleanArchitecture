package data.local

import com.example.demokmpapp.database.Database
import kotlinx.coroutines.flow.Flow
import data.local.DataEntity

interface UserDao {
    fun getAllUser(): Flow<List<DataEntity>>
    suspend fun getUserById(id: Long): DataEntity?
    suspend fun insertUser(user: DataEntity)
    suspend fun updateUser(user: DataEntity)
    suspend fun deleteUser(id: Long)
    suspend fun deleteAllUsers()

    suspend fun createUser(user: DataEntity)
}



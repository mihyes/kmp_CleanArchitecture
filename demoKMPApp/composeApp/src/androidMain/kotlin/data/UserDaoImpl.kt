package data

import com.example.demokmpapp.database.Database
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.local.DataEntity
import data.local.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map


class UserDaoImpl(
    private val database: Database
): UserDao {
    private val queries = database.databaseQueries

    override fun getAllUser(): Flow<List<DataEntity>> {
        return queries.getAllUsers()
            .asFlow()
            .mapToList(Dispatchers.Main)
            .map { users ->
                users.map { user ->
                    DataEntity(
                        id = user.id,
                        name = user.name,
                        version = user.version,
                        platform = user.platform
                    )
                }
            }
    }



    override suspend fun getUserById(id: Long): DataEntity? {
        return queries.getUserById(id).executeAsOneOrNull()?.let { user ->
            DataEntity(
                id = user.id,
                name = user.name,
                version = user.version,
                platform = user.platform
            )
        }

    }


    override suspend fun insertUser(user: DataEntity) {
        queries.insertUser(
            id = user.id,
            name = user.name,
            version = user.version,
            platform = user.platform
        )
    }


    override suspend fun updateUser(user: DataEntity) {
        queries.updateUser(
            id = user.id,
            name = user.name,
            version = user.version,
            platform = user.platform
        )
    }

    override suspend fun deleteUser(id: Long) {
        queries.deleteUser(id = id)
    }

    override suspend fun deleteAllUsers() {
        queries.deleteAllUsers()
    }

    override suspend fun createUser(user: DataEntity) {
        queries.insertUser(
            id = user.id,
            name = user.name,
            version = user.version,
            platform = user.platform
        )
    }
}
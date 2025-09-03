package data.repository

import com.example.demokmpapp.Platform
import data.local.DataEntity
import data.local.UserDao
import data.remote.UserApi
import data.remote.CreateUserRequest
import domain.model.User
import domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
//import kotlinx.datetime.toEpochMilliseconds



// Repository 패턴 구현


class UserRepositoryImpl(
    private val userApi: UserApi,
    private val userDao: UserDao
) : UserRepository {

    override fun getUser(): Flow<List<User>> {
        return userDao.getAllUser().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)?.toDomainModel()
    }

    override suspend fun refreshUsers() {
        try {
            val remoteUser = userApi.getUsers()
            userDao.deleteAllUsers()
            remoteUser.forEach { dto ->
                userDao.insertUser(dto.toEntity())
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createUser(name: String, platform: String): User {
        val request = CreateUserRequest(name, platform)
        val remoteUser = userApi.createUser(request)
        val entity = remoteUser.toEntity()
        userDao.insertUser(entity)
        return entity.toDomainModel()
    }


    override suspend fun updateUser(id: Long, name: String, platform: String): User {
        val request = CreateUserRequest(name, platform)
        val remoteUser = userApi.updateUser(id, request)
        val entity = remoteUser.toEntity()
        userDao.updateUser(entity)

        return entity.toDomainModel()
    }

    override suspend fun deleteUser(id: Long) {
        userApi.deleteUser(id)
        userDao.deleteUser(id)
    }



    // ===========================================
    // 🗄️ 새로운 로컬 전용 방식
    // ===========================================

    /**
     * 🆕 로컬 데이터베이스에만 사용자 생성 (API 호출 없음)
     */

    override suspend fun createUserLocally(name: String, platform: String): User {
        val now = Clock.System.now().toEpochMilliseconds()

        val localId = generateLocalId()
        val entiry = DataEntity(
            id = localId,
            name = name,
            version = "2025.9.3",
            platform = platform
        )

        userDao.insertUser(entiry)
        return entiry.toDomainModel()
    }


    /**
     * 🆕 로컬 데이터베이스에만 사용자 생성 (API 호출 없음)
     */
    override suspend fun updateUserLocally(id: Long, name: String, platform: String): User {
        val existingUser = userDao.getUserById(id)?:throw IllegalArgumentException("User does not exist")

        val updateEntity = existingUser.copy(
            id = id,
            name = name,
            version = "2025.9.3",
            platform = platform
        )

        userDao.updateUser(updateEntity)
        return updateEntity.toDomainModel()
    }

    override suspend fun deleteUserLocally(id: Long) {
        userDao.deleteUser(id)
    }

    override suspend fun clearUserLocally() {
        userDao.deleteAllUsers()
    }



    /**
     * 로컬 ID 생성 (음수 사용하여 서버 ID와 구분)
     * 서버 ID는 양수, 로컬 ID는 음수로 관리
     */
    private suspend fun generateLocalId(): Long {
        // 현재 시간을 기반으로 음수 ID 생성
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val random = Random.nextInt(1000, 9999)
        return -(timestamp + random)
    }

}


private fun data.local.DataEntity.toDomainModel(): User = User (
    id = id,
    name = name,
    version = version,
    platform = platform
)

private fun data.remote.UserDto.toEntity() = data.local.DataEntity (
    id = id,
    name = name,
    version = version,
    platform = platform
)
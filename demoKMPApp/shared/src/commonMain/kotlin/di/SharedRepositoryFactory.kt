package di

import data.local.UserDao
import data.remote.UserApiImpl
import data.repository.UserRepositoryImpl
import domain.repository.UserRepository

object SharedRepositoryFactory {
    fun createUserRepository(userDao: UserDao): UserRepository {
        val httpClient = NetworkModule.provideHttpClient()
        val userApi = UserApiImpl(httpClient)

        return UserRepositoryImpl(userApi, userDao)
    }


}
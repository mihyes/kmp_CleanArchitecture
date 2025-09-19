package di

import data.local.UserDao
import data.remote.UserApi
import domain.repository.UserRepository
import data.remote.UserApiImpl
import data.repository.UserRepositoryImpl
import io.ktor.client.HttpClient

object RepositoryFactory {

    fun createUserRepository(userDao: UserDao): UserRepository {
        val httpClient = NetworkModule.provideHttpClient()
        val userApi = createUserApi(httpClient)

        return UserRepositoryImpl(
            userApi = userApi,
            userDao = userDao
        )
    }


    private fun createUserApi(httpClient: HttpClient): UserApi {
        return UserApiImpl(httpClient)
    }



    fun provideUserRepository(userDao: UserDao, userApi: UserApi): UserRepository {
        return UserRepositoryImpl(userApi, userDao)
    }

}
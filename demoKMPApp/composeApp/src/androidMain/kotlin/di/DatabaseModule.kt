package di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import com.example.demokmpapp.database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import data.local.UserDao
import javax.inject.Singleton

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import data.UserDaoImpl

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "app.db"
        )
    }



    @Provides
    @Singleton
    fun provideDatabase(driver: SqlDriver): Database {
        return Database(driver)
    }

    @Provides
    fun provideUserDao(database: Database): UserDao {
        return data.UserDaoImpl(database)
     }


//    @Provides
//    fun provideUserDao(database: Database): UserDao {
//        // 임시로 간단한 구현체 생성
//        return object : UserDao {
//            override fun getAllUsers(): Flow<List<User>> = flow { emit(emptyList()) }
//            override suspend fun insertUser(user: User) {}
//            override suspend fun updateUser(user: User) {}
//            override suspend fun deleteUser(id: Long) {}
//            // 다른 필요한 메서드들도 빈 구현...
//        }
//    }
}
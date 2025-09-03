package di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import data.local.UserDao
import data.UserDaoImpl
import com.example.demokmpapp.database.Database
import data.local.DataEntity
import org.koin.dsl.module

actual val PlatformModule = module {
    single<SqlDriver> {
        NativeSqliteDriver(Database.Schema, "user.db")
    }

    single { Database(get()) }
    single<UserDao> { UserDaoImpl(get()) }

}
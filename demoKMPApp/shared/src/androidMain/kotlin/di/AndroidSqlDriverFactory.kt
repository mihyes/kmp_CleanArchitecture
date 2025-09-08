package di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.demokmpapp.database.Database
import data.UserDaoImpl
import data.local.UserDao
import org.koin.android.ext.koin.androidContext

class AndroidSqlDriverFactory {
    fun createDriver(context: Context): SqlDriver {
        return AndroidSqliteDriver(
            schema = Database.Companion.Schema,
            context = context,
            name = "app.db"
        )
    }

    fun createDatabase(context: Context): Database {
        return Database.Companion(createDriver(context))
    }


}

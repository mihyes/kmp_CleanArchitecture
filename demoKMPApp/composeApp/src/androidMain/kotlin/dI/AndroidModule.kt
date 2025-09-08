//package dI
//
//import androidx.compose.ui.util.fastCbrt
//import com.example.demokmpapp.database.Database
//import data.UserDaoImpl
//import data.local.UserDao
//import di.AndroidSqlDriverFactory
//import domain.repository.UserRepository
//import domain.usecase.*
//import org.koin.android.ext.koin.androidContext
//import org.koin.androidx.viewmodel.dsl.viewModel
//import org.koin.core.module.dsl.factoryOf
//import org.koin.dsl.module
//
//
//var androidModule = module {
//
//    single<Database> {
//        AndroidSqlDriverFactory.createDatabase(androidContext())
//    }
//
//    single<UserDao> {
//        UserDaoImpl(get())
//    }
//
//
//    // ===========================================
//    // 📦 REPOSITORY (shared 구현체 사용)
//    // ===========================================
//
//    single<UserRepository> {
//       SharedRepositoryFactory.createUserRepository(get())
//    }
//
//
//    factoryOf(::GetUserUseCase)
//    factoryOf(::CreateUserUseCase)
//    factoryOf(::RefreshUserUseCase)
//
//}
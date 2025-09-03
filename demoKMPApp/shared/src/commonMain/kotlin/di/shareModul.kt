//package di
//
//import data.remote.UserApi
//import data.remote.UserApiImpl
//import data.repository.UserRepositoryImpl
//import domain.repository.UserRepository
//import domain.usecase.GetUserUseCase
//import domain.usecase.CreateUserUseCase
//import domain.usecase.RefreshUserUseCase
//import presentation.UserPresenter
//
//import io.ktor.client.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.plugins.logging.*
//import io.ktor.serialization.kotlinx.json.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.serialization.json.Json
//import org.koin.core.module.dsl.singleOf
//import org.koin.dsl.bind
//import org.koin.dsl.module
//
//val shareModule = module {
//    single {
//        HttpClient {
//            install(ContentNegotiation) {
//                json( Json {
//                    ignoreUnknownKeys = true
//                    isLenient = true
//                })
//            }
//            install(Logging) {
//                level = LogLevel.INFO
//            }
//        }
//    }
//
//    // API
//    singleOf(::UserApiImpl) bind UserApi::class
//
//
//    // Repository
//    singleOf(::UserRepositoryImpl) bind UserRepository::class
//
//    //UseCase
//    singleOf(::GetUserUseCase)
//    singleOf(::CreateUserUseCase)
//    singleOf(::RefreshUserUseCase)
//
//    // Presenter
//    factory {
//        UserPresenter(
//            getUserUseCase = get(),
//            createUserUseCase = get(),
//            refreshUserUseCase = get(),
//            coroutineScope = CoroutineScope(SupervisorJob())
//        )
//    }
//}
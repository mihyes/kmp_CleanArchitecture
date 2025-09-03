package domain.usecase

import com.example.demokmpapp.Platform
import domain.repository.UserRepository
import domain.model.User

class CreateUserLocallyUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(name: String, platform: String): Result<User> {
        return try {
            if (name.isBlank()) {
                Result.failure(IllegalArgumentException("Name cannot be blank"))
            } else if (platform.isBlank()) {
                Result.failure(IllegalArgumentException("platform cannot be blank"))
            } else {
                val user = userRepository.createUserLocally(name, platform)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
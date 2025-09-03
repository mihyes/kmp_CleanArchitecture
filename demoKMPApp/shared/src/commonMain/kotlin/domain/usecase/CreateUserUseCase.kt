package domain.usecase

import com.example.demokmpapp.Platform
import domain.repository.UserRepository
import domain.model.User


class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(name: String, platform: String): Result<User> {
        return try {
            if (name.isBlank() || platform.isBlank()) {
                Result.failure(IllegalArgumentException("Name and platform cannot be blank"))
            } else {
                // API
                val user = userRepository.createUser(name, platform)
                // DB
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}

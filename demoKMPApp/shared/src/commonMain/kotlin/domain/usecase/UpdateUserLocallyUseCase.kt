package domain.usecase

import domain.repository.UserRepository
import domain.model.User


class UpdateUserLocallyUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(id: Long, name: String, platform: String): Result<User> {
        return try {
            if (name.isBlank()) {
                Result.failure(IllegalArgumentException("Name cannot be blank"))
            } else if (platform.isBlank()) {
                Result.failure(IllegalArgumentException("Email cannot be blank"))
            } else {
                val user = userRepository.updateUserLocally(id, name, platform)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}
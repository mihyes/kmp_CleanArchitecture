package domain.usecase

import domain.repository.UserRepository

class ClearLocalUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            userRepository.clearUserLocally()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
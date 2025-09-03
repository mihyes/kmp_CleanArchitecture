package domain.usecase

import domain.repository.UserRepository

class DeleteUserLocallyUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            userRepository.deleteUserLocally(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
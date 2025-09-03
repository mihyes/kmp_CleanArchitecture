package domain.usecase

import domain.model.User
import kotlinx.coroutines.flow.Flow
import domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> {
        return userRepository.getUser()
    }
}
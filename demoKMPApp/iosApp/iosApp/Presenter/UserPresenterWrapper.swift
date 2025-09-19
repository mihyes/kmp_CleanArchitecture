//
//  UserViewnModel.swift
//  iosApp
//
//  Created by mhkim on 9/2/25.
//

import Foundation
import SwiftUI
@preconcurrency import shared
import Combine
import KMPNativeCoroutinesAsync


// MARK: - 타입 별칭 정의 (KMP 타입 매핑)
typealias User = User_
//typealias UserUiState = UserUiState


protocol UserPresenterWrapperProtocol: Sendable {
		func getUsers() async throws -> [User]
		func createUserLocally(name: String, platform: String) async throws
		func deleteUserLocally(id: Int64) async throws
		func clearDatabase() async throws
		func getCurrentUsers() async -> [User]
		func isLoading() async -> Bool
		func getCurrentError() async -> String?
		func clearError() async
}


//@MainActor
class UserPresenterWrapper: UserPresenterWrapperProtocol, @unchecked Sendable {
		
		// 현재 상태를 캐시하여 동기적 접근 지원
		@Published private var currentState: UserUiState?
		
		private let presenter: IOSUserPresenter
		private var cancellables = Set<AnyCancellable>()
		private let serialQueue = DispatchQueue(label: "UserPresenterWrapper", qos: .userInitiated)
		
		var statePublisher: AnyPublisher<UserUiState, Never> {
				$currentState
						.compactMap { $0 }
						.eraseToAnyPublisher()
		}
		
		
		var usersPublisher: AnyPublisher<[User], Never> {
				$currentState
						.compactMap { $0?.users }
						.eraseToAnyPublisher()
		}
		
		
		var isLoadingPublisher: AnyPublisher<Bool, Never> {
				$currentState
						.map { $0?.isLoading ?? false }
						.eraseToAnyPublisher()
		}
		
		
		var errorPublisher: AnyPublisher<String?, Never> {
				$currentState
						.map { $0?.error }
						.eraseToAnyPublisher()
		}
		
		
		
		// init
		init(presenter: IOSUserPresenter) {
				self.presenter = presenter
		}
		
		
		deinit {
				presenter.cleanup()
		}
		
		
		
		
		private func setupStateObservation() {
				let statePublisher = PassthroughSubject<UserUiState, Never>()
				
				presenter.observeUiState { [weak self] state in
								statePublisher.send(state)
				}
				
				statePublisher
						.receive(on: DispatchQueue.main)
						.sink { state in
								self.currentState = state
						}
						.store(in: &cancellables)
		}
		
		
		
		func getUsers() async throws -> [User] {
				return try await presenter.getUsers()
		}
		
		func createUserLocally(name: String, platform: String) async throws {
				try await presenter.createUserLocallyAsync(name: name, platform: platform)
		}
		
		
		func deleteUserLocally(id: Int64) async throws {
				try await presenter.deleteUserLocallyAsync(id: id)
		}
		
		func clearDatabase() async throws {
				try await presenter.clearDatabaseAsync()
		}
		
		func getCurrentUsers() -> [User] {
				return currentState?.users as? [User] ?? []
		}
		
		func isLoading() -> Bool {
				return currentState?.isLoading ?? false
		}
		
		func getCurrentError() -> String? {
				return currentState?.error
		}
		
		func clearError() {
				presenter.clearError()
		}
		
		
}
		
		
		
		
		
		//
		//
		//		@MainActor
		//		func saveAfGetUsers() async throws -> [User_] {
		//				return try await withCheckedThrowingContinuation { continuation in
		//						presenter.observeUiState { state in
		//								if let mapped = state.users as? [User_] {
		//										let userCopy = mapped.map { user in
		//												User_(id: user.id,
		//															name: user.name,
		//															version: user.version,
		//															platform: user.platform)
		//										}
		//										continuation.resume(returning: userCopy)
		//								} else {
		//										continuation.resume(throwing: UserRepositoryError.noDataFound)
		//								}
		//						}
		//				}
		//		}
		//
		//		/**
		//		 * 서버에서 사용자 새로고침
		//		 */
		//		func refreshUser() {
		//				presenter.refreshUsers()
		//
		//		}
		//
		//		func createUser(name: String, platform: String) async throws {
		//				guard !name.isEmpty else {
		//						throw NSError(domain: "ValidationError", code: 1001, userInfo: [NSLocalizedDescriptionKey : "Name is required."])
		//				}
		//
		//				presenter.createUser(name: name, platform: platform, onSuccess: {
		//						DispatchQueue.main.async {
		//								print("User '\(name)' created successfully")
		//						}
		//				}, onError: { errorMessage in
		//						DispatchQueue.main.async {
		//								self.errorMessage = errorMessage
		//								print("errorMessage: \(errorMessage)")
		//						}
		//				})
		//
		//		}
		//
		//
		//		// ===========================================
		//		// 🗄️ 로컬 전용 메서드들 (새로 추가)
		//		// ===========================================
		//
		////		func createUserLocally(name: String, platform: String) {
		////				guard !name.isEmpty, !platform.isEmpty else {
		////						self.errorMessage = "Name and email cannot be empty"
		////						return
		////				}
		////
		////				presenter.createUserLocally(name: name, platform: platform) {
		////						DispatchQueue.main.async {
		////								print("User '\(name)' created successfully")
		////								self.startObserving()
		////						}
		////				} onError: { errorMessage in
		////						DispatchQueue.main.async {
		////								self.errorMessage = errorMessage
		////								print("errorMessage: \(errorMessage)")
		////						}
		////				}
		////		}
		//
		//
		////		func createUserLocally(name: String, platform: String) async throws {
		////				try await withCheckedThrowingContinuation { continuation in
		////						guard !name.isEmpty, !platform.isEmpty else {
		////								self.errorMessage = "Name and email cannot be empty"
		////								continuation.resume(throwing: UserRepositoryError.userNotFound)
		////								return
		////						}
		////
		////						presenter.createUserLocally(name: name, platform: platform) {
		////
		//////								continuation.resume(returning: true)
		////
		////						} onError: { errorMessage in
		////								self.errorMessage = errorMessage
		////								continuation.resume(throwing: UserRepositoryError.databaseError)
		////						}
		////				}
		////		}
		//
		//
		//		func createUserLocally(name: String, platform: String) async throws {
		//				await withCheckedContinuation { continuation in
		//						guard !name.isEmpty, !platform.isEmpty else {
		//								self.errorMessage = "Name and email cannot be empty"
		//								return
		//						}
		//
		//						presenter.createUserLocally(name: name, platform: platform) {
		//								continuation.resume()
		//
		//						} onError: { errorMessage in
		//								self.errorMessage = errorMessage
		//								return
		//						}
		//				}
		//		}
		//
		//
		//
		////
		////		func updateUserLocal(id: Int, name: String, platform: String) {
		////				guard !name.isEmpty, !platform.isEmpty else {
		////						self.errorMessage = "Name and email cannot be empty"
		////						return
		////				}
		////
		////				presenter.updateUserLocally(id: Int64(id), name: name, platform: platform) {
		////						DispatchQueue.main.async {
		////								print("User '\(name)' updated successfully")
		////						}
		////				} onError: { errorMessage in
		////						DispatchQueue.main.async {
		////								self.errorMessage = errorMessage
		////								print("errorMessage: \(errorMessage)")
		////						}
		////				}
		////		}
		//
		//		func updateUserLocally(id: Int, name: String, platform: String) async throws -> Bool {
		//				return try await withCheckedThrowingContinuation { continuation in
		//						guard !name.isEmpty, !platform.isEmpty else {
		//								self.errorMessage = "Name and email cannot be empty"
		//								continuation.resume(throwing: UserRepositoryError.userNotFound)
		//								return
		//						}
		//
		//						presenter.updateUserLocally(id: Int64(id), name: name, platform: platform) {
		//								continuation.resume(returning: true)
		//
		//						} onError: { errorMessage in
		//								self.errorMessage = errorMessage
		//								continuation.resume(throwing: UserRepositoryError.databaseError)
		//						}
		//				}
		//		}
		//
		//
		//
		//
		////
		////		func deleteUserLocally(id: Int) async throws -> Bool {
		////				return try await withCheckedThrowingContinuation { continuation in
		////						presenter.deleteUserLocally(id: Int64(id)) {
		////								continuation.resume(returning: true)
		////						}	onError: { errorMessage in
		////								self.errorMessage = errorMessage
		////								continuation.resume(throwing: UserRepositoryError.databaseError)
		////						}
		////				}
		////		}
		//
		//		func deleteUserLocally(id: Int) async {
		//				return await withCheckedContinuation { continuation in
		//						presenter.deleteUserLocally(id: Int64(id)) {
		//								continuation.resume()
		//						}	onError: { errorMessage in
		//								self.errorMessage = errorMessage
		//						}
		//				}
		//		}
		//
		////		func deleteUserLocal(id: Int) {
		////				presenter.deleteUserLocally(id: Int64(id)) {
		////						DispatchQueue.main.async {
		////								print("User deleted successfully")
		////						}
		////				} onError: { errorMessage in
		////						DispatchQueue.main.async {
		////								self.errorMessage = errorMessage
		////								print("errorMessage: \(errorMessage)")
		////						}
		////				}
		////		}
		//
		//
		//
		//		func clearUserLocally() async {
		//				return await withCheckedContinuation { continuation in
		//						presenter.clearUserLocally {
		//								continuation.resume()
		//						}	onError: { errorMessage in
		//								self.errorMessage = errorMessage
		//						}
		//				}
		//		}
		//
		////		func clearUsersLocal() {
		////				presenter.clearUserLocally {
		////						DispatchQueue.main.async {
		////								print("User cleared successfully")
		////						}
		////				} onError: { errorMessage in
		////						DispatchQueue.main.async {
		////								self.errorMessage = errorMessage
		////								print("errorMessage: \(errorMessage)")
		////						}
		////				}
		////
		////		}
		////
		//
		//		func clearMessages() {
		//				presenter.clearError()
		//				self.errorMessage = nil
		//		}
		//
		//
		////
		////		func isLocalUser(_ user: User) -> Bool {
		////				return user.id < 0
		////		}
		//
		//
		//		var localUsers: [User_] {
		//				return users.filter { $0.id < 0}
		//		}
		//
		//		var serverUSers: [User_] {
		//				return users.filter { $0.id > 0 }
		//		}
		//
		//}
		//
		//
		


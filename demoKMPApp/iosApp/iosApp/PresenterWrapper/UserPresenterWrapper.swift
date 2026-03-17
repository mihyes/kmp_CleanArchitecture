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
				
				presenter.observeUiState { state in
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

		// MARK: - Server Methods
		func refreshUsersFromServer() async throws {
				presenter.refreshUsers()
				// Wait a moment for the refresh to complete
				try await Task.sleep(nanoseconds: 500_000_000)
		}

		func createUserOnServer(name: String, platform: String) async throws {
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
						presenter.createUser(
								name: name,
								platform: platform,
								onSuccess: {
										continuation.resume()
								},
								onError: { errorMessage in
										continuation.resume(throwing: NSError(domain: "UserPresenter", code: -1, userInfo: [NSLocalizedDescriptionKey: errorMessage]))
								}
						)
				}
		}


}
		
		
	

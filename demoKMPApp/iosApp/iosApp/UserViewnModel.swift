//
//  UserViewnModel.swift
//  iosApp
//
//  Created by mhkim on 9/2/25.
//

import Foundation
import SwiftUI
import shared
import Combine
import KMPNativeCoroutinesAsync


@MainActor
class UserViewnModel: ObservableObject {
		@Published var users: [User_] = []
		@Published var isLoading: Bool = false
		@Published var error: String? = nil
	
//		private let iosPresenter: IOSUserPresenter
		private let presenter: IOSUserPresenter
		private var cancellables = Set<AnyCancellable>()
		
		
		init() {
				self.presenter = DIContainer().getIOSUserPresenter()
//				self.presenter = DIContainer().getUserPresenter()
				startObserving()
		}
		
//		
		func startObserving() {
				// ✅ StateFlow가 자동으로 Swift Publisher로 변환됨
				presenter.observeUiState { [weak self] state in
						DispatchQueue.main.async {
								self?.users = state.users
								self?.isLoading = state.isLoading
								self?.error = state.error
						}
				}

				
		}

		/**
		 * 서버에서 사용자 새로고침
		 */
		func refreshUser() {
				presenter.refreshUsers()
				
		}
		
		func createUser(name: String, platform: String) async throws {
				guard !name.isEmpty else {
						throw NSError(domain: "ValidationError", code: 1001, userInfo: [NSLocalizedDescriptionKey : "Name is required."])
				}
				
				presenter.createUser(name: name, platform: platform, onSuccess: {
						DispatchQueue.main.async {
								print("User '\(name)' created successfully")
						}
				}, onError: { errorMessage in
						DispatchQueue.main.async {
								self.error = errorMessage
								print("errorMessage: \(errorMessage)")
						}
				})
				
		}
		
		
		// ===========================================
		// 🗄️ 로컬 전용 메서드들 (새로 추가)
		// ===========================================
		
		func createUserLocal(name: String, platform: String) {
				guard !name.isEmpty, !platform.isEmpty else {
						self.error = "Name and email cannot be empty"
						return
				}
				
				presenter.createUserLocally(name: name, platform: platform) {
						DispatchQueue.main.async {
								print("User '\(name)' created successfully")
								self.startObserving()
						}
				} onError: { errorMessage in
						DispatchQueue.main.async {
								self.error = errorMessage
								print("errorMessage: \(errorMessage)")
						}
				}

//				iosPresenter.createUserLocal(name: name, platform: platform)

//				presenter.createUserLocally(name: name, platform: platform) { [weak self] result in
//						DispatchQueue.main.async {
//								switch result {
//								case .success(let user):
//										print("User '\(user.name)' created successfully")
//								case .failure(let error):
//								}
//						}
//				}

				
		}
		
		
		func updateUserLocal(id: Int, name: String, platform: String) {
				guard !name.isEmpty, !platform.isEmpty else {
						self.error = "Name and email cannot be empty"
						return
				}
				
				presenter.updateUserLocally(id: Int64(id), name: name, platform: platform) {
						DispatchQueue.main.async {
								print("User '\(name)' updated successfully")
						}
				} onError: { errorMessage in
						DispatchQueue.main.async {
								self.error = errorMessage
								print("errorMessage: \(errorMessage)")
						}
				}

		}
		
		
		func deleteUserLocal(id: Int) {
				presenter.deleteUserLocally(id: Int64(id)) {
						DispatchQueue.main.async {
								print("User deleted successfully")
						}
				} onError: { errorMessage in
						DispatchQueue.main.async {
								self.error = errorMessage
								print("errorMessage: \(errorMessage)")
						}
				}

		}
		
		
		func clearUsersLocal() {
				presenter.clearUserLocally {
						DispatchQueue.main.async {
								print("User cleared successfully")
						}
				} onError: { errorMessage in
						DispatchQueue.main.async {
								self.error = errorMessage
								print("errorMessage: \(errorMessage)")
						}
				}

		}
		
		
		func clearMessages() {
				presenter.clearError()
				self.error = nil
		}
		

		
		func isLocalUser(_ user: User) -> Bool {
				return user.id < 0
		}

		
		var localUsers: [User_] {
				return users.filter { $0.id < 0}
		}
		
		var serverUSers: [User_] {
				return users.filter { $0.id > 0 }
		}

}

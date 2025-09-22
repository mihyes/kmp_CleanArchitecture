//
//  UserFeature.swift
//  iosApp
//
//  Created by mhkim on 9/16/25.
//

import ComposableArchitecture
import Foundation
@preconcurrency import shared


@Reducer
struct UserFeature {
		
		@ObservableState
		struct State: Equatable {
				var users: [User] = []
				var isLoading = false
				var errorMessage: String?
				var newUserName = ""
				var newUserPlatform = "iOS"
				var isAddingUser = false
				var selectedUserId: Int64?
				var isEditingUser = false
		}
		
		enum Action {
				case onAppear
				case loadUsers
				case loadUsersResponse(TaskResult<[User]>)
				
				case saveUserLocally(String)
				case saveUserLocallyResponse(TaskResult<Void>)
				
				case deleteUserLocally(Int64)
				case deleteUserLocallyResponse(TaskResult<Void>)
				
				case clearDatabase
				case clearDatabaseResponse(TaskResult<Void>)
				
				
				case newUserNameChanged(String)
				case newUserPlatformChanged(String)
				
				//				case toggleAddingUser
				//				case startEditingUser(Int64)
				//				case toggleEditingUser
				//				case dismissError
				
		}
		
		
		
		@Dependency(\.userPresenterWrapper) var userRepository
		@Dependency(\.mainQueue) var mainQueue
		
		
		
		var body: some ReducerOf<Self> {
				Reduce { state, action in
						switch action {
						case .onAppear:
								return .send(.loadUsers)
								
						case .loadUsers:
								state.isLoading = true
								state.errorMessage = nil
								return .run { send in
										await send(.loadUsersResponse(
												TaskResult {
														try await userRepository.getUsers()
														
												}
										))
								}
								
						case .loadUsersResponse(.success(let users)):
								state.isLoading = false
								state.users = users
								return .none
								
						case.loadUsersResponse(.failure(let error)):
								state.isLoading = false
								state.errorMessage = error.localizedDescription
								return .none
								
						case .saveUserLocally(let name):
								guard !name.isEmpty else {
										state.errorMessage = "User name is required."
										return .none
								}
								state.isLoading = true
								return .run { [name = name, platform = state.newUserPlatform] send in
										await send(.saveUserLocallyResponse(
												TaskResult {
														try await userRepository.createUserLocally(name: name, platform: platform)
												}
										))
								}
								
								
						case .saveUserLocallyResponse(.success):
								state.isLoading = false
								state.newUserName = ""
								state.newUserPlatform = "iOS"
								state.isAddingUser = false
								return .send(.loadUsers)
								
						case .saveUserLocallyResponse(.failure(let error)):
								state.isLoading = false
								state.errorMessage = error.localizedDescription
								return .none
								
								
						case .deleteUserLocally(let userId):
								state.isLoading = true
								return .run { send in
										await send(.deleteUserLocallyResponse(
												TaskResult {
														try await userRepository.deleteUserLocally(id: userId)
												}
										))
								}
								
						case .deleteUserLocallyResponse(.success):
								state.isLoading = false
								return .send(.loadUsers)
								
						case .deleteUserLocallyResponse(.failure(let error)):
								state.isLoading = false
								state.errorMessage = error.localizedDescription
								return .none
								
								
								
						case .clearDatabase:
								state.isLoading = true
								return .run { send in
										await send(.clearDatabaseResponse(
												TaskResult {
														try await userRepository.clearDatabase()
												}
										))
								}
								
						case .clearDatabaseResponse(.success):
								state.isLoading = false
								state.users = []
								return .none
								
								
						case .clearDatabaseResponse(.failure(let error)):
								state.isLoading = false
								state.errorMessage = error.localizedDescription
								return .none
								
								
								
						case let .newUserNameChanged(name):
								state.newUserName = name
								return .none
								
						case let .newUserPlatformChanged(platform):
								state.newUserPlatform = platform
								return .none
								
						}
				}
				
		}
}

//
//  UserRepositoryDependency.swift
//  iosApp
//
//  Created by mhkim on 9/16/25.
//

import Dependencies
import Foundation
import shared


// MARK: - Sendable 준수를 위한 확장
extension User_: @unchecked @retroactive Sendable {}
extension UserUiState: @unchecked @retroactive Sendable {}


extension DependencyValues {
		var userPresenterWrapper: UserPresenterWrapperProtocol {
				get { self[UserPresenterWrapperKey.self] }
				set { self[UserPresenterWrapperKey.self] = newValue }
		}
}

/*
 @preconcurrency:
 Swift Concurrency 도입 이전의 레거시 프로토콜과 상호작용할 때 “이 conformace는 concurrency를 신경 안 써도 된다” 정도의 힌트를 주는 속성'
 
 DependencyKey는 nonisolated 속성을 가지므로 @MainActor은 X
 -> DependencyKey.liveValue는 nonisolated 전역(static) 프로퍼티 → 모든 Task/actor에서 접근 가능
    UserPresenterWrapper가 Sendable이 아니라서 다른 actor에저 접근하면 위험 ( @MainActor class Sendable이 아님 )
 */

private enum UserPresenterWrapperKey: DependencyKey {
//		static let liveValue: UserPresenterWrapperProtocol =
//		MainActor.assumeIsolated {
//				UserPresenterWrapper(presenter: DIContainer().getIOSUserPresenter())
//		}
//		
		static let liveValue: UserPresenterWrapperProtocol = {
				let diContainer = DIContainer()
				let iosPresenter = diContainer.getIOSUserPresenter()
				return UserPresenterWrapper(presenter: iosPresenter)
		}()
}



/*  사용 예시
 
 @Dependency(\.userPresenterWrapper) var makeUserPresenter

 func someFeature() {
		 let wrapper = makeUserPresenter() // 호출 시점에 MainActor 보장
		 // wrapper 사용
 }
 
 */

//protocol UserPresenterWrapperProtocol {
//		func getUsers() async throws -> [User]
//		func createUserLocally(name: String, platform: String) async throws
//		func createUser(name: String, platform: String) async throws -> User
//		func clearDatabase() async throws
//		func deleteUser(id: Int64) async throws
//		func deleteUserLocally(id: Int64) async throws
//}


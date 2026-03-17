//
//  UserPresenterWrapperProtocol.swift
//  iosApp
//
//  Created by mhkim on 9/22/25.
//

import Foundation
import SwiftUI
@preconcurrency import shared
import Combine
import KMPNativeCoroutinesAsync



protocol UserPresenterWrapperProtocol: Sendable {
		func getUsers() async throws -> [User]
		func createUserLocally(name: String, platform: String) async throws
		func deleteUserLocally(id: Int64) async throws
		func clearDatabase() async throws
		func getCurrentUsers() async -> [User]
		func isLoading() async -> Bool
		func getCurrentError() async -> String?
		func clearError() async

		// Server methods
		func refreshUsersFromServer() async throws
		func createUserOnServer(name: String, platform: String) async throws
}

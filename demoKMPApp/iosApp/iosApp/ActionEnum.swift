//
//  ActionEnum.swift
//  iosApp
//
//  Created by mhkim on 9/15/25.
//

import Foundation

enum ActionEnum: String {
		case saveDB
		case removeDB
		case getDataInDB
		case refreshDB
}



enum UserRepositoryError: LocalizedError {
		case unknownError
		case networkError
		case databaseError
		case userNotFound
		case noDataFound
		
		var errorDescription: String? {
				switch self {
				case .unknownError:
						return "알 수 없는 오류가 발생했습니다"
				case .networkError:
						return "네트워크 연결을 확인해주세요"
				case .databaseError:
						return "데이터베이스 오류가 발생했습니다"
				case .userNotFound:
						return "사용자를 찾을 수 없습니다"
				case .noDataFound:
						return "데이터를 찾을 수 없습니다"
				}
		}
}





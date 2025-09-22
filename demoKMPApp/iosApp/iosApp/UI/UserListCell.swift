//
//  UserListCell.swift
//  iosApp
//
//  Created by mhkim on 9/22/25.
//

import Foundation
import SwiftUI
import shared
import ComposableArchitecture

struct UserListCell: View {
		let user: User_
		
		var body: some View {
				HStack {
						VStack(alignment: .leading, spacing: 4) {
								Text(user.name)
										.font(.headline)
										.fontWeight(.bold)
								Text("Platform: " + user.platform)
										.font(.subheadline)
										.foregroundColor(.secondary)
								Text("로컬 사용자")
										.font(.subheadline)
										.foregroundColor(.blue)
										
						}
						
						Spacer()
						
						Image(systemName: "pencil")
								.resizable()
								.scaledToFit()
								.frame(width: 15, height: 15)
								.padding(20)
						
						Image(systemName: "trash")
								.resizable()
								.scaledToFit()
								.frame(width: 15, height: 15)
								.padding(.trailing, 20)
				}
				.padding(.vertical, 4)
		}
}

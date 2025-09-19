import UIKit
import SwiftUI
import shared
import ComposableArchitecture




struct ContentView: View {
		let store: StoreOf<UserFeature>
		@State var name: String = ""
		
    var body: some View {
				WithViewStore(store, observe: { $0 }) { viewStore in
						NavigationView {
								VStack {
										Button("Refesh Users") {
												viewStore.send(.loadUsers)
										}
										.padding()
										VStack {
												TextField("please enter name", text: $name)
												Button("Add Users") {
														viewStore.send(.saveUserLocally(name))
												}
										}
										
										.padding()
										
										if viewStore.isLoading {
												ProgressView("Loading....")
														.frame(maxWidth: .infinity, maxHeight: .infinity)
										} else if let error = viewStore.errorMessage {
												Text("Error: \(error)")
														.foregroundColor(.red)
														.padding()
										} else {
												List(viewStore.users, id: \.id) { user in
														UserRowView(user: user)
												}
										}
								}
								.navigationTitle("User")
								.onChange(of: viewStore.users) { oldValue, newValue in
										print("users List: \(newValue)")
								}
						}
				}
				.onAppear {
//						viewModel.startObserving()
				}
    }
}


struct UserRowView: View {
		let user: User_
		
		var body: some View {
				VStack(alignment: .leading, spacing: 4) {
						Text(user.name)
								.font(.headline)
						Text(user.platform)
								.font(.subheadline)
								.foregroundColor(.secondary)
				}
				.padding(.vertical, 4)
		}
}



//struct EditContentView: View {
//		let store: StoreOf<UserFeature>
//		
//		
//		var body: some View {
//				WithViewStore(store, observe: { $0 }) { viewStore in
//						<#code#>
//				} content: { viewStore in
//						<#code#>
//				}
//
//		}
//}



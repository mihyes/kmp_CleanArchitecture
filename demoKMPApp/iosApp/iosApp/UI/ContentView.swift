import UIKit
import SwiftUI
import shared
import ComposableArchitecture




struct ContentView: View {
		let store: StoreOf<UserFeature>
		@State var name: String = ""
		@FocusState private var isFocused: Bool
		
		
    var body: some View {
				WithViewStore(store, observe: { $0 }) { viewStore in
						NavigationView {
								VStack {
										HStack {
												Button("새로고침") {
														viewStore.send(.loadUsers)
														isFocused = false
												}
												.foregroundStyle(Color.white)
												.frame(width: 80, height: 30)
												.background(.purple)
												Spacer()
										}
										.frame(height: 50)
										.padding()
										
										VStack {
												TextField("please enter name", text: $name)
														.textFieldStyle(RoundedBorderTextFieldStyle())
														.focused($isFocused)
												HStack {
														Button("사용자 추가") {
																viewStore.send(.saveUserLocally(name))
																self.name = ""
																isFocused = false
														}
														.frame(width: 80, height: 30)
														.background(.purple)
														.foregroundStyle(Color.white)
														Spacer()
												}
										}
										.frame(height: 80)
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
														UserListCell(user: user)
												}
												.background(Color.white.opacity(0.1))
										}
										
										Spacer()
								}
								.navigationTitle("DB User List")
								.onChange(of: viewStore.users) { oldValue, newValue in
										print("users List: \(newValue)")
								}
								.onAppear {
										viewStore.send(.loadUsers)
								}
						}
						
				}
    }
}




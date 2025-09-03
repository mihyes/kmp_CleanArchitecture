import UIKit
import SwiftUI
//import ComposeApp
import shared

//struct ComposeView: UIViewControllerRepresentable {
//    func makeUIViewController(context: Context) -> UIViewController {
//        MainViewControllerKt.MainViewController()
//    }
//
//    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
//}

struct ContentView: View {
		@StateObject private var viewModel = UserViewnModel()
		@State var name: String = ""
		
    var body: some View {
				NavigationView {
						VStack {
								Button("Refesh Users") {
										viewModel.refreshUser()
								}
								.padding()
								
								VStack {
										TextField("please enter name", text: $name)
										Button("Add Users") {
												viewModel.createUserLocal(name: name, platform: "iOS")
										}
								}
								
								.padding()
								
								if viewModel.isLoading {
										ProgressView("Loading....")
												.frame(maxWidth: .infinity, maxHeight: .infinity)
								} else if let error = viewModel.error {
										Text("Error: \(error)")
												.foregroundColor(.red)
												.padding()
								} else {
										List(viewModel.users, id: \.id) { user in
												UserRowView(user: user)
										}
								}
						}
						.navigationTitle("User")
						.onChange(of: viewModel.users) { oldValue, newValue in
								print("users List: \(newValue)")
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

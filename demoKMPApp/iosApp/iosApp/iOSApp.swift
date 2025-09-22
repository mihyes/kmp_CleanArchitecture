import SwiftUI
import ComposableArchitecture

@main
struct iOSApp: App {
		var body: some Scene {
				WindowGroup {
						ContentView(store: Store(initialState: UserFeature.State()) {
								UserFeature()  // 전체 버전 사용
						})
						
				}
		}
}

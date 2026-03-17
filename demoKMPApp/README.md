# KMP Clean Architecture Demo

Kotlin Multiplatform + Clean Architecture 기반 사용자 관리 데모 앱

브랜치: `develop`

## 개요

Android, iOS, Web, Server를 단일 코드베이스로 지원하는 Kotlin Multiplatform 프로젝트.
Clean Architecture 패턴을 적용하여 플랫폼별 UI와 공통 비즈니스 로직을 분리했다.

## 프로젝트 구조

```
demoKMPApp/
├── shared/                    # 공유 모듈 (Domain, Data, Presentation)
│   ├── commonMain/            # 공통 코드
│   │   ├── domain/            # UseCase, Repository Interface, Model
│   │   ├── data/              # Repository Impl, API, DAO
│   │   └── di/                # DI 설정
│   ├── androidMain/           # Android 전용 코드
│   ├── iosMain/               # iOS 전용 코드 (IOSUserPresenter)
│   ├── jvmMain/               # JVM 전용 코드
│   └── jsMain/                # JS/Web 전용 코드
├── composeApp/                # Android 앱 (Compose + Hilt)
├── iosApp/                    # iOS 앱 (SwiftUI + TCA)
├── webApp/                    # Web 앱 (React + Vite)
└── server/                    # Ktor 서버 (REST API)
```

## 실행 방법

### 1. 서버 실행
```bash
./gradlew :server:run
# → http://localhost:8080
```

### 2. Android 앱
```bash
./gradlew :composeApp:assembleDebug
# 또는 Android Studio에서 실행
```

### 3. iOS 앱
```bash
# Xcode에서 iosApp/iosApp.xcodeproj 열기
# 최초 실행 시 Swift 매크로 승인 필요 (Trust & Enable)
```

### 4. Web 앱
```bash
cd webApp
npm install
npm run dev
# → http://localhost:5173
```

## 아키텍처

```
📱 Android/iOS/Web (Presentation Layer)
    ↓ ViewModel/Presenter를 통해 UseCase 호출
🎭 Domain Layer (UseCase)
    ↓ Repository Interface를 통해 데이터 요청
📦 Data Layer (Repository Implementation)
    ↓ API 호출 또는 로컬 DB 접근
🌐 Network Layer (Ktor Client) / 🗄️ Local DB (SQLDelight)
```

## 주요 기능

| 기능 | 설명 |
|------|------|
| 사용자 목록 조회 | 서버/로컬 DB에서 사용자 목록 가져오기 |
| 사용자 추가 | 서버 또는 로컬 DB에 사용자 생성 |
| 사용자 삭제 | 서버/로컬에서 사용자 삭제 |
| 서버 새로고침 | 서버 데이터를 로컬 DB에 동기화 |
| 서버 UI | 브라우저에서 사용자 목록 HTML 페이지 확인 |

## 플랫폼별 기술 스택

| 플랫폼 | UI | DI | 상태관리 |
|--------|----|----|----------|
| Android | Jetpack Compose | Hilt | StateFlow |
| iOS | SwiftUI | TCA Dependencies | TCA (Composable Architecture) |
| Web | React | - | useState/useEffect |
| Server | Ktor (HTML) | - | In-Memory |

## API 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/` | 서버 상태 및 사용자 목록 HTML |
| GET | `/users` | 모든 사용자 조회 (JSON) |
| GET | `/users/{id}` | 특정 사용자 조회 |
| POST | `/users` | 새 사용자 생성 |
| PUT | `/users/{id}` | 사용자 수정 |
| DELETE | `/users/{id}` | 사용자 삭제 |

## 의존성

### Shared Module
- Ktor Client (네트워킹)
- SQLDelight (로컬 DB)
- Kotlinx Serialization (JSON)
- Kotlinx Coroutines
- Koin (DI)
- KMP-NativeCoroutines (iOS async/await 지원)

### Android
- Jetpack Compose
- Hilt (DI)
- AndroidX Lifecycle

### iOS
- SwiftUI
- TCA (The Composable Architecture)
- KMPNativeCoroutinesAsync

### Server
- Ktor Server Netty
- Ktor Content Negotiation
- Ktor CORS

### Web
- React
- Vite
- TypeScript

## 설정

### 서버 URL (플랫폼별)

| 플랫폼 | BASE_URL | 파일 |
|--------|----------|------|
| Android | `http://10.0.2.2:8080` | `Constants.android.kt` |
| iOS | `http://localhost:8080` | `Constants.ios.kt` |
| Web | `http://localhost:8080` | `UserList.tsx` |

### 네트워크 설정
- **Android**: `usesCleartextTraffic="true"` (AndroidManifest.xml)
- **iOS**: `NSAllowsLocalNetworking` (Info.plist)
- **Server**: CORS 허용 (localhost:5173, localhost:3000)

## 버전 히스토리

| 버전 | 변경 내용 |
|------|----------|
| v1.0.0 | 초기 프로젝트 설정, Android/iOS 로컬 DB 연동 |
| v1.1.0 | Ktor 서버 API 구현, CRUD 기능 |
| v1.2.0 | 웹앱 추가, 서버 UI HTML 페이지 |
| v1.3.0 | iOS 서버 연동 (새로고침, 사용자 추가) |

## 참고

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [The Composable Architecture](https://github.com/pointfreeco/swift-composable-architecture)
- [Ktor](https://ktor.io/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)

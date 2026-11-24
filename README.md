# Melodia: High-Fidelity Mobile Music Streaming App

Melodia is a cutting-edge native Android music streaming client built with modern **Kotlin**, **Jetpack Compose**, and **Material Design 3 (M3)**. Engineered with Clean Architecture and unidirectional data flow (UDF), Melodia delivers an immersive, studio-grade listening experience featuring 24-bit/192kHz Hi-Res Lossless audio streaming with **AndroidX Media3 ExoPlayer**, background playback via **MediaSessionService**, hardware-backed **AES-256-GCM** encryption for offline tracks, real-time synchronized lyrics (**Live Synced LRC Lyrics**), and an aesthetic Dark Theme tailored for modern audiophiles.

---

## 🎧 Core Features

### 1. Discovery & Home Experience

- **Quick Picks**: 2-column dynamic grid for fast access to favorite tracks and mixes.
- **Jump Back In**: Resumes unfinished playlists and recent listening sessions seamlessly.
- **Sonic Chemistry**: Algorithmic music taste compatibility card (e.g., 98% Match).
- **Featured Artists & New Releases**: Curated spotlights on trending artists and newly released albums.

### 2. Real-Time Search & Exploration

- **300ms Debounce Query Pipeline**: Eliminates redundant network and compute overhead during typing ([NFR-02: Search Latency < 1s]).
- **Multi-Category Best Match**: Tabbed and segmented result cards covering Songs, Artists, Albums, and Playlists.
- **Recent Searches & Trending Tags**: Quick-access chips and search history management when the query input is cleared.

### 3. Background Audio Playback & Media3 Architecture

- **Persistent Mini Player (76dp)**: Compact, floating playback bar visible across all navigation tabs with play/pause and progress tracking. Expandable to full screen with a single tap.
- **Now Playing Screen**: Full transport controls (Shuffle, Previous, Play/Pause, Next, Repeat), interactive seekbar, Hi-Res Lossless badge, and instant favorite toggle.
- **Foreground MediaSessionService**: Robust background execution with system notification controls, lock screen media integration, and audio focus management (automatically pauses on headset disconnect or incoming calls).

### 4. Live Synced Lyrics

- **Real-Time LRC Parser**: Synchronizes timestamped song lyrics line-by-line with millisecond precision.
- **"NOW SINGING" Visual Focus**: Automatically highlights and enlarges the current vocal line with smooth auto-scrolling.

### 5. Smart Sleep Timer

- **Flexible Timer Presets**: 5, 15, 30, 45, 60 minutes, or at the end of the current track.
- **Smooth 30-Second Fade-Out**: Prevents abrupt silence by smoothly ramping audio volume down during the final 30 seconds before pausing playback.

### 6. Library & Playlist Management

- **Pinned Favorites**: Quick access to the "Liked Songs" collection.
- **Custom Playlists**: Create, rename, delete, and add/remove songs with optimistic UI updates and instant state reflection.

### 7. Secure Offline Downloads

- **Device Storage Distribution**: Visual bar breakdown of local storage (Melodia audio cache vs. other apps vs. free space).
- **AES-256-GCM Hardware-Backed Encryption**: Encrypts downloaded audio files using cryptographic keys securely generated and stored in the **AndroidKeyStore** ([NFR-06: Security & Privacy]).
- **Airplane Mode Ready**: Fully functional offline playback with zero network dependency.

### 8. Authentication & Guest Experience

- **Encrypted Session Persistence**: Secure token and user credential storage using `EncryptedSharedPreferences`.
- **Guest Preview Mode**: Enforces a 30-second preview limit on tracks for unauthenticated guest listeners while safeguarding premium lossless content.
- **Tuning Your Sonic DNA**: Onboarding genre picker allowing new users to select their favorite musical styles to calibrate personalized recommendations.

### 9. Accessibility & Internationalization

- **Dynamic Font Scaling**: Adaptive layout support adhering to system typography preferences ([NFR-12: Accessibility]).
- **Dual Localization**: Full bilingual localization with Vietnamese (`values-vi/strings.xml`) and English (`values/strings.xml`).

---

## 🏗 System Architecture

Melodia follows Google's recommended **Clean Architecture** combined with the **MVVM (Model-View-ViewModel)** architectural pattern:

```powershell
mobile/app/src/main/kotlin/com/melodia/app/
├── domain/                  # Enterprise & Business Logic Layer (Framework Independent)
│   ├── model/               # Immutable domain entities (Song, Artist, Album, Playlist, User)
│   └── repository/          # Repository interfaces defining business contracts
├── data/                    # Data Access & Storage Layer
│   ├── repository/          # Repository implementations (Auth, Catalog, Playlist, Player, Downloads)
│   ├── security/            # Keystore encryption (AesCipherManager) and secure storage (TokenStore)
│   └── local/               # Mock data provider and DataStore preferences
├── service/                 # Android System Services
│   └── player/              # AndroidX Media3 MediaSessionService and PlayerNotificationManager
├── presentation/            # UI Layer (Jetpack Compose & Material 3)
│   ├── auth/                # Login, Registration, and Sonic DNA genre onboarding
│   ├── home/                # Discovery feed and quick picks
│   ├── search/              # Real-time search with debounce
│   ├── library/             # Library overview and Playlist details
│   ├── downloads/           # Offline downloads manager and storage analyzer
│   ├── player/              # Now Playing, Live Lyrics, Sleep Timer, and Queue sheets
│   ├── artist/              # Artist profile and Album detail screens
│   ├── settings/            # User settings, audio quality, and cache controls
│   ├── common/              # Reusable UI components (MiniPlayer, SongItemRow, SongActionSheet)
│   └── theme/               # Dark Cyberpunk/Synthwave color palette, typography, and shapes
└── di/                      # Dependency Injection Container (AppContainer)
```

---

## 🛠 Technology Stack

| Layer / Concern | Technology |
| --- | --- |
| **Programming Language** | Kotlin 2.3+ |
| **UI Framework** | Jetpack Compose (BOM 2026.03.01) |
| **Design System** | Material Design 3 (M3) Dark Theme |
| **Audio Engine** | AndroidX Media3 ExoPlayer & MediaSession 1.5.1 |
| **Concurrency & State** | Kotlin Coroutines & StateFlow (Unidirectional Data Flow) |
| **Image Loading** | Coil Compose 2.7.0 |
| **Security & Cryptography** | AndroidKeyStore, AES-256-GCM, `androidx.security.crypto` |
| **Local Storage** | Jetpack DataStore Preferences 1.1.1 |
| **Background Tasks** | AndroidX WorkManager 2.10.0 |
| **Networking** | Retrofit 2.11.0, OkHttp 4.12.0 |
| **Unit Testing** | JUnit 4.13.2, `kotlinx-coroutines-test` 1.10.2 |

---

## 🚀 Build & Installation Guide

### Prerequisites

- **Java Development Kit**: JDK 17 or JDK 21
- **Android SDK**: Compile SDK 36, Target SDK 36, Min SDK 26 (Android 8.0+)
- **Android Build Tools**: 36.0.0

### Gradle Commands

> [!NOTE]
> All Gradle commands must be executed from inside the `mobile/` directory where `gradlew.bat` is located:

```powershell
cd mobile
```

1. **Compile Kotlin Sources**:

   ```powershell
   .\gradlew.bat compileDebugKotlin
   ```

2. **Run All Unit Tests**:

   ```powershell
   .\gradlew.bat testDebugUnitTest
   ```

   *Executes comprehensive test suites validating Catalog, Playlist, Player, and Download repositories.*

3. **Assemble Debug APK**:

   ```powershell
   .\gradlew.bat assembleDebug
   ```

   *Generates the installation APK at `mobile/app/build/outputs/apk/debug/app-debug.apk` (~24.9 MB, comfortably below the 50 MB requirement).*

---

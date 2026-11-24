# Melodia Backend API - Lossless Music Streaming Service

> **HCMUT - Ho Chi Minh City University of Technology**
> **Course:** Mobile Application Development (CO3043) - Class CC01 - Assignment 1
> **Architecture:** Clean Architecture / RESTful Microservice
> **Stack:** Node.js (TypeScript) & Express, PostgreSQL 16, JWT Dual-Token Authentication, Docker Compose

---

## 🌟 Overview

The **Melodia Backend** is a production-grade, high-fidelity RESTful service engineered specifically to power the **Melodia Android Music Streaming App**. Built in full compliance with **Section 5 (Data & API Design)** of the Assignment 1 specification, it features:

- 🎵 **Hi-Res Lossless Audio Streaming:** Serves 24-bit/96kHz FLAC & 320kbps MP3 audio metadata with bitrate attribution.
- ⏱️ **Synchronized LRC Lyrics Engine:** Microsecond-accurate parsing and delivery of LRC lyrics for real-time karaoke sync.
- 🔐 **Dual-Token JWT Security:** Stateless 15-minute access tokens and 7-day refresh tokens conforming to `NFR-06`.
- 👤 **Guest Preview Mode:** Allows unauthenticated preview streaming capped at 30 seconds per track.
- 💾 **Hardware-Backed Offline Caching:** Issues AES-256-GCM download tokens verified by Android KeyStore.
- 🎧 **Sonic Chemistry Discovery (98% Match):** Dynamic recommendation engine delivering personalized mixes and quick picks.
- 🗄️ **Comprehensive 17-Entity Relational Schema:** Strictly models users, devices, artists, albums, songs, genres, playlists, licenses, and playback analytics.
- ⚡ **Zero-Dependency Fallback Mode:** Seamlessly operates out-of-the-box using a high-fidelity in-memory data store when PostgreSQL is not running.

---

## 🏗️ Architecture & 17-Entity Data Model

The database strictly implements the 17 relational entities defined in Section 5.1 of the Assignment 1 report:

```text
[users] ──┬── [devices]
          ├── [favorites] ────── [songs] ──┬── [song_artists] ──── [artists]
          ├── [play_history] ──── [songs]   ├── [song_genres] ───── [genres]
          ├── [downloads] ─────── [songs]   ├── [licenses]
          └── [playlists] ─────── [playlist_songs] ── [songs]
                                       │
                                   [albums] ── [album_artists] ── [artists]
                                       │
                                [playback_queues] ── [queue_items] ── [songs]
```

### PostgreSQL DDL & Seed Scripts

- **Schema (17 Tables & Indexes):** [src/database/schema.sql](file:///src/database/schema.sql)
- **Seed Catalog (Synchronized with Android MockDataProvider):** [src/database/seed.sql](file:///src/database/seed.sql)

---

## 🚀 Quick Start Guide

### Prerequisites & Storage Optimization

- **Node.js:** v18+ (Node.js 20 LTS or v24 recommended)
- **Package Manager:** `pnpm` (Highly Recommended) or `npm`

> **Disk Space Optimization Tip:**  
> Using **`pnpm`** utilizes a centralized, content-addressable storage model on your machine (`pnpm store`). If a package version already exists in your global cache, `pnpm` creates a lightweight hard link instead of duplicating node modules on disk, saving hundreds of megabytes of disk space and preventing disk bloat.

---

### Option A: Running with Node.js & TypeScript (Instant In-Memory Fallback)

Melodia includes an intelligent auto-detection layer. If PostgreSQL is not active, the backend immediately activates the **In-Memory Store** seeded with full lossless catalog data, requiring zero external database setup!

#### Step 1: Navigate to the backend directory

```powershell
cd backend
```

#### Step 2: Install dependencies (Storage-efficient with pnpm)

```powershell
pnpm install
# or with npm:
# npm install
```

#### Step 3: Start the development server

```powershell
pnpm run dev
# or with npm:
# npm run dev
```

**Console Output:**

```text
----------------------------------------------------
🎵 Melodia Lossless Music Streaming Backend Server
   HCMUT CO3043 Mobile Development - Group 14 (CC01)
----------------------------------------------------
⚠️ PostgreSQL unavailable. Falling back to High-Fidelity In-Memory Data Store.
🚀 Server listening on port 3000
📍 Local URL:     http://localhost:3000/v1
📱 Emulator URL:  http://10.0.2.2:3000/v1
📖 Swagger Docs:  http://localhost:3000/v1/docs
🩺 Health Check:  http://localhost:3000/health
----------------------------------------------------
```

#### Step 4: Production Build & Execution

```powershell
pnpm run build
pnpm start
```

---

### Option B: Running with Docker Compose (Full PostgreSQL 16)

For a complete containerized environment with PostgreSQL 16 and automated database migration:

#### Step 1: Launch containers

```powershell
docker compose up --build -d
```

#### Step 2: Verify running services

```powershell
docker compose ps
```

#### Step 3: View backend logs

```powershell
docker compose logs -f backend
```

#### Step 4: Tear down containers

```powershell
docker compose down -v
```

---

## 📖 Interactive Swagger / OpenAPI Documentation

Once the backend is running, open your browser to inspect and execute API requests interactively:

👉 **[http://localhost:3000/v1/docs](http://localhost:3000/v1/docs)**

---

## 📡 REST API Reference & Sample cURL Requests

### Base URLs

- **Local Workstation:** `http://localhost:3000/v1`
- **Android Emulator:** `http://10.0.2.2:3000/v1`
- **Physical Device:** `http://<YOUR_LAN_IP>:3000/v1`

---

### 1. Authentication (`/v1/auth`)

#### Register a new user

```bash
curl -X POST http://localhost:3000/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alex.rivera@melodia.stream",
    "password": "Password123!",
    "displayName": "Alex Rivera",
    "selectedGenres": ["Synthwave", "Cyberpunk", "Lo-Fi Chill"]
  }'
```

#### Log in as Registered User

```bash
curl -X POST http://localhost:3000/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alex.rivera@melodia.stream",
    "password": "Password123!"
  }'
```

#### Log in as Guest (Instant 30-second Preview Mode)

```bash
curl -X POST http://localhost:3000/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "isGuest": true
  }'
```

---

### 2. Discovery & Home Feed (`/v1/home`)

Retrieves the **Sonic Chemistry 98% Match**, Quick Picks, Jump Back In, Curated Playlists, and Popular Artists:

```bash
curl -X GET http://localhost:3000/v1/home \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>"
```

---

### 3. Songs & Lossless Audio Streaming (`/v1/songs`)

#### Get Song Details

```bash
curl -X GET http://localhost:3000/v1/songs/s_01
```

#### Get Lossless Streaming URL (24-bit/96kHz FLAC)

```bash
curl -X GET "http://localhost:3000/v1/songs/s_01/stream?quality=lossless" \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>"
```

*Note: For guest tokens, `previewLimitSec: 30` is automatically enforced.*

#### Get Synchronized LRC Lyrics

```bash
curl -X GET http://localhost:3000/v1/songs/s_01/lyrics
```

*Response includes raw LRC string and pre-parsed `lines: [{ timestampMs: 14000, text: "..." }]` for ExoPlayer synchronization.*

---

### 4. Search (`/v1/search`)

Unified multi-entity search across songs, artists, albums, and playlists:

```bash
curl -X GET "http://localhost:3000/v1/search?q=cyberpunk&type=all"
```

---

### 5. Playlists CRUD (`/v1/playlists` & `/v1/me/playlists`)

#### Create a Playlist

```bash
curl -X POST http://localhost:3000/v1/playlists \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Late Night Coding",
    "description": "Lo-fi synth beats for deep focus sessions",
    "isPublic": true
  }'
```

#### Add Song to Playlist

```bash
curl -X POST http://localhost:3000/v1/playlists/pl_01/songs \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "songId": "s_02"
  }'
```

#### Get User's Playlists

```bash
curl -X GET http://localhost:3000/v1/me/playlists \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>"
```

---

### 6. User Library & Favorites (`/v1/me`)

#### Toggle Like or Favorite Song

```bash
# Like song (PUT)
curl -X PUT http://localhost:3000/v1/me/favorites/s_01 \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>"

# Unlike song (DELETE)
curl -X DELETE http://localhost:3000/v1/me/favorites/s_01 \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>"
```

#### Record Playback History

```bash
curl -X POST http://localhost:3000/v1/me/history \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "songId": "s_01",
    "durationPlayedSec": 180,
    "completed": true
  }'
```

---

### 7. Offline Download & AES-256 Licensing (`/v1/downloads`)

Requests a cryptographic download license token for encrypted local storage:

```bash
curl -X POST http://localhost:3000/v1/downloads \
  -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "songId": "s_01",
    "deviceId": "android_pixel_9_pro"
  }'
```

---

## 🛡️ Error Response Specification

All API errors strictly conform to the unified error schema specified on **Page 40 of Assignment 1**:

```json
{
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "Email address already registered.",
    "details": [],
    "requestId": "req_a9f12b_1740001234"
  }
}
```

| HTTP Status | Error Code | Description |
| :--- | :--- | :--- |
| `400 Bad Request` | `VALIDATION_FAILED` | Missing or invalid request parameters |
| `401 Unauthorized` | `UNAUTHORIZED` / `TOKEN_EXPIRED` | Bearer token is missing, corrupted, or expired |
| `403 Forbidden` | `DOWNLOAD_NOT_ALLOWED` | Guest users restricted from offline caching |
| `404 Not Found` | `SONG_NOT_FOUND` / `PLAYLIST_NOT_FOUND` | Resource identifier does not exist |
| `409 Conflict` | `EMAIL_EXISTS` / `SONG_ALREADY_IN_PLAYLIST` | Unique constraint violation |
| `500 Server Error` | `INTERNAL_SERVER_ERROR` | Unexpected backend runtime failure |

---

## 📱 Connecting Android Mobile App to Backend

The Melodia Android application is pre-configured with Retrofit and automatic fallback. When testing with the Android Emulator:

### Step 1: Start the backend server

```powershell
cd backend
pnpm run dev
```

### Step 2: Backend Address in Emulator

The Android Emulator maps `http://10.0.2.2:3000` to the host machine's `localhost:3000`.

### Step 3: Android Network Security Configuration

The app's `AndroidManifest.xml` enables `android:usesCleartextTraffic="true"` for debug builds, allowing seamless HTTP communication with `10.0.2.2:3000`.

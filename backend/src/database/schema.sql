-- Melodia Database Schema (PostgreSQL)
-- Specification based on Section 5.1 of CO3043 Mobile Application Development Assignment Report
-- 17 Core Entities: User Management, Catalogue, Library, and Playback

-- 1. Users Entity
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(64) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    avatar_url TEXT,
    role VARCHAR(32) DEFAULT 'user' CHECK (role IN ('user', 'admin', 'moderator')),
    is_guest BOOLEAN DEFAULT FALSE,
    selected_genres TEXT[] DEFAULT '{}',
    status VARCHAR(32) DEFAULT 'active' CHECK (status IN ('active', 'suspended', 'deactivated')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Devices Entity
CREATE TABLE IF NOT EXISTS devices (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) REFERENCES users(id) ON DELETE CASCADE,
    device_name VARCHAR(120) NOT NULL,
    device_model VARCHAR(120),
    os_version VARCHAR(64),
    last_active TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Artists Entity
CREATE TABLE IF NOT EXISTS artists (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    bio TEXT,
    image_url TEXT,
    banner_url TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    monthly_listeners INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Albums Entity
CREATE TABLE IF NOT EXISTS albums (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    cover_url TEXT,
    release_date DATE,
    album_type VARCHAR(32) DEFAULT 'album' CHECK (album_type IN ('album', 'single', 'ep', 'compilation')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Album Artists (Many-to-Many)
CREATE TABLE IF NOT EXISTS album_artists (
    album_id VARCHAR(64) REFERENCES albums(id) ON DELETE CASCADE,
    artist_id VARCHAR(64) REFERENCES artists(id) ON DELETE CASCADE,
    role VARCHAR(64) DEFAULT 'primary',
    PRIMARY KEY (album_id, artist_id)
);

-- 6. Genres Entity
CREATE TABLE IF NOT EXISTS genres (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(120) UNIQUE NOT NULL,
    description TEXT,
    color_hex VARCHAR(16) DEFAULT '#6750A4'
);

-- 7. Licenses Entity (Music licensing for streaming & downloads)
CREATE TABLE IF NOT EXISTS licenses (
    id VARCHAR(64) PRIMARY KEY,
    license_type VARCHAR(64) DEFAULT 'standard',
    can_stream BOOLEAN DEFAULT TRUE,
    can_download BOOLEAN DEFAULT TRUE,
    max_devices INT DEFAULT 3,
    expires_at TIMESTAMP WITH TIME ZONE
);

-- 8. Songs Entity
CREATE TABLE IF NOT EXISTS songs (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    album_id VARCHAR(64) REFERENCES albums(id) ON DELETE SET NULL,
    license_id VARCHAR(64) REFERENCES licenses(id) ON DELETE SET NULL,
    duration_sec INT NOT NULL,
    stream_url TEXT NOT NULL,
    download_url TEXT,
    lyrics_lrc TEXT,
    bitrate_kbps INT DEFAULT 320,
    is_lossless BOOLEAN DEFAULT TRUE,
    lossless_format VARCHAR(64) DEFAULT 'FLAC 24-bit / 96kHz',
    play_count BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9. Song Artists (Many-to-Many)
CREATE TABLE IF NOT EXISTS song_artists (
    song_id VARCHAR(64) REFERENCES songs(id) ON DELETE CASCADE,
    artist_id VARCHAR(64) REFERENCES artists(id) ON DELETE CASCADE,
    role VARCHAR(64) DEFAULT 'primary' CHECK (role IN ('primary', 'featured', 'producer')),
    PRIMARY KEY (song_id, artist_id)
);

-- 10. Song Genres (Many-to-Many)
CREATE TABLE IF NOT EXISTS song_genres (
    song_id VARCHAR(64) REFERENCES songs(id) ON DELETE CASCADE,
    genre_id VARCHAR(64) REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (song_id, genre_id)
);

-- 11. Playlists Entity
CREATE TABLE IF NOT EXISTS playlists (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cover_url TEXT,
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 12. Playlist Songs Entity (Ordered Songs in Playlist)
CREATE TABLE IF NOT EXISTS playlist_songs (
    playlist_id VARCHAR(64) REFERENCES playlists(id) ON DELETE CASCADE,
    song_id VARCHAR(64) REFERENCES songs(id) ON DELETE CASCADE,
    position INT NOT NULL,
    added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (playlist_id, song_id)
);

-- 13. Favorites Entity
CREATE TABLE IF NOT EXISTS favorites (
    user_id VARCHAR(64) REFERENCES users(id) ON DELETE CASCADE,
    song_id VARCHAR(64) REFERENCES songs(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, song_id)
);

-- 14. Play History Entity
CREATE TABLE IF NOT EXISTS play_history (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(64) REFERENCES users(id) ON DELETE CASCADE,
    song_id VARCHAR(64) REFERENCES songs(id) ON DELETE CASCADE,
    duration_played_sec INT DEFAULT 0,
    completed BOOLEAN DEFAULT FALSE,
    played_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 15. Playback Queues Entity
CREATE TABLE IF NOT EXISTS playback_queues (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) REFERENCES users(id) ON DELETE CASCADE,
    is_shuffle BOOLEAN DEFAULT FALSE,
    repeat_mode VARCHAR(16) DEFAULT 'off' CHECK (repeat_mode IN ('off', 'all', 'one')),
    current_index INT DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 16. Queue Items Entity
CREATE TABLE IF NOT EXISTS queue_items (
    id BIGSERIAL PRIMARY KEY,
    queue_id VARCHAR(64) REFERENCES playback_queues(id) ON DELETE CASCADE,
    song_id VARCHAR(64) REFERENCES songs(id) ON DELETE CASCADE,
    position INT NOT NULL
);

-- 17. Downloads Entity
CREATE TABLE IF NOT EXISTS downloads (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(64) REFERENCES devices(id) ON DELETE CASCADE,
    song_id VARCHAR(64) REFERENCES songs(id) ON DELETE CASCADE,
    status VARCHAR(32) DEFAULT 'completed' CHECK (status IN ('pending', 'downloading', 'completed', 'failed')),
    size_bytes BIGINT DEFAULT 0,
    license_token TEXT,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, device_id, song_id)
);

-- Indexes for optimal performance
CREATE INDEX IF NOT EXISTS idx_songs_title ON songs(title);
CREATE INDEX IF NOT EXISTS idx_artists_name ON artists(name);
CREATE INDEX IF NOT EXISTS idx_playlists_user ON playlists(user_id);
CREATE INDEX IF NOT EXISTS idx_favorites_user ON favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_history_user ON play_history(user_id, played_at DESC);

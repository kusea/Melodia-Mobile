-- Seed Data for Melodia PostgreSQL Database
-- Synchronized with Mobile Client's MockDataProvider.kt

-- Insert Default Admin & Test Users (Password: S3cure!pass)
-- bcrypt hash for 'S3cure!pass': $2a$10$w8gZ9d3s5Ew6Q0zHk1B6u.H11c0Kz9wQ9T3zB4i6v0a.G11c0Kz9w
INSERT INTO users (id, email, password_hash, display_name, role, is_guest, selected_genres)
VALUES 
    ('u_admin', 'admin@melodia.app', '$2a$10$3p8C6K89L.6QeI4Yf4Q3heU0fO0m8vD8uL5W2h1b6v0a.G11c0Kz9', 'System Admin', 'admin', FALSE, ARRAY['Synthwave', 'Electronic']),
    ('u_01', 'linh@example.com', '$2a$10$3p8C6K89L.6QeI4Yf4Q3heU0fO0m8vD8uL5W2h1b6v0a.G11c0Kz9', 'Linh Nguyen', 'user', FALSE, ARRAY['Synthwave', 'Lo-Fi', 'Electronic', 'Cyberpunk']),
    ('u_guest', 'guest@melodia.local', '$2a$10$3p8C6K89L.6QeI4Yf4Q3heU0fO0m8vD8uL5W2h1b6v0a.G11c0Kz9', 'Guest Explorer', 'user', TRUE, ARRAY['Synthwave'])
ON CONFLICT (id) DO NOTHING;

-- Insert Artists
INSERT INTO artists (id, name, bio, image_url, is_verified, monthly_listeners)
VALUES
    ('art_01', 'Aurora Waves', 'Pioneering ambient synthwave soundscapes with analog hardware and immersive binaural depth.', 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500', TRUE, 1250000),
    ('art_02', 'Cyber Dreamer', 'Futuristic synthpop combining cyberpunk themes with soaring melodic leads.', 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500', TRUE, 890000),
    ('art_03', 'Luna Eclipse', 'Cinematic chillstep, lo-fi beats, and nocturnal piano melodies.', 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500', TRUE, 640000),
    ('art_04', 'Retro Boy', 'Pure 80s nostalgia, outrun arpeggios, and vintage drum machines.', 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500', FALSE, 420000)
ON CONFLICT (id) DO NOTHING;

-- Insert Albums
INSERT INTO albums (id, title, cover_url, release_date, album_type)
VALUES
    ('alb_01', 'Midnight Odyssey', 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500', '2025-10-15', 'album'),
    ('alb_02', 'Cyberpunk Retrospect', 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500', '2025-11-20', 'album'),
    ('alb_03', 'Echoes of the Night', 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500', '2026-01-10', 'ep')
ON CONFLICT (id) DO NOTHING;

-- Insert Album Artists
INSERT INTO album_artists (album_id, artist_id)
VALUES
    ('alb_01', 'art_01'),
    ('alb_02', 'art_02'),
    ('alb_03', 'art_03')
ON CONFLICT DO NOTHING;

-- Insert Genres
INSERT INTO genres (id, name, description, color_hex)
VALUES
    ('gen_synthwave', 'Synthwave', '80s retro-futuristic synthesizers, gated reverbs and neon aesthetics', '#6750A4'),
    ('gen_cyberpunk', 'Cyberpunk', 'High-tech low-life gritty industrial beats and dystopian synths', '#00F0FF'),
    ('gen_lofi', 'Lo-Fi Chill', 'Warm vinyl cracks, relaxed drum grooves and soothing rhodes chords', '#FFB74D'),
    ('gen_ambient', 'Ambient', 'Expansive atmospheric textures designed for deep focus and sleep', '#4CAF50'),
    ('gen_electronic', 'Electronic', 'Dynamic EDM, techno and high-energy melodic bass', '#E91E63'),
    ('gen_rock', 'Rock', 'Classic guitars, heavy riffs and raw vocal expressions', '#F44336')
ON CONFLICT (id) DO NOTHING;

-- Insert Licenses
INSERT INTO licenses (id, license_type, can_stream, can_download, max_devices)
VALUES
    ('lic_std_01', 'commercial_lossless', TRUE, TRUE, 5),
    ('lic_std_02', 'commercial_lossless', TRUE, TRUE, 5)
ON CONFLICT (id) DO NOTHING;

-- Insert Songs
INSERT INTO songs (id, title, album_id, license_id, duration_sec, stream_url, lyrics_lrc, bitrate_kbps, is_lossless, lossless_format, play_count)
VALUES
    ('s_01', 'Midnight City Drive', 'alb_01', 'lic_std_01', 214, 'https://cdn.melodia.example/stream/midnight_city_drive.flac', 
     '[00:00.00]Cruising under the neon lights\n[00:15.50]The city hums beneath the tires\n[00:32.10]Electric shadows fade to night\n[00:48.80]Chasing our midnight desires\n[01:05.20]Feel the bassline through the floor\n[01:21.00]Never wanting anything more\n[01:40.40]Synthesized dreams in purple glow\n[02:00.00]Where the endless highways flow',
     1411, TRUE, 'FLAC 24-bit / 96kHz', 89400),
     
    ('s_02', 'Neon Horizon', 'alb_02', 'lic_std_01', 198, 'https://cdn.melodia.example/stream/neon_horizon.flac',
     '[00:00.00]The skyline glows in crimson red\n[00:18.00]Thoughts of the future in my head\n[00:35.00]Speeding toward the morning light\n[00:54.00]Leaving behind the quiet night',
     1411, TRUE, 'FLAC 24-bit / 96kHz', 67300),

    ('s_03', 'Echoes in the Rain', 'alb_03', 'lic_std_01', 245, 'https://cdn.melodia.example/stream/echoes_in_the_rain.flac',
     '[00:00.00]Raindrops falling on the glass\n[00:22.00]Watching all the moments pass\n[00:44.00]Soft echoes in the empty street\n[01:08.00]Where our lost memories meet',
     1411, TRUE, 'FLAC 24-bit / 192kHz', 45100),

    ('s_04', 'Synthesized Love', 'alb_01', 'lic_std_01', 186, 'https://cdn.melodia.example/stream/synthesized_love.flac',
     '[00:00.00]Analog heart in a digital world\n[00:20.00]Watch the synthetic love unfurl\n[00:40.00]Binary pulses beat in time\n[01:02.00]Your frequency perfectly matches mine',
     1411, TRUE, 'FLAC 24-bit / 96kHz', 38200),

    ('s_05', 'Starlight Serenade', 'alb_02', 'lic_std_01', 230, 'https://cdn.melodia.example/stream/starlight_serenade.flac',
     '[00:00.00]Stars ignite across the dark\n[00:25.00]A gentle whisper, a glowing spark\n[00:50.00]Floating free in outer space\n[01:15.00]Finding peace in this warm embrace',
     1411, TRUE, 'FLAC 24-bit / 96kHz', 51000)
ON CONFLICT (id) DO NOTHING;

-- Associate Songs with Artists
INSERT INTO song_artists (song_id, artist_id)
VALUES
    ('s_01', 'art_01'),
    ('s_02', 'art_02'),
    ('s_03', 'art_03'),
    ('s_04', 'art_04'),
    ('s_05', 'art_01')
ON CONFLICT DO NOTHING;

-- Associate Songs with Genres
INSERT INTO song_genres (song_id, genre_id)
VALUES
    ('s_01', 'gen_synthwave'),
    ('s_02', 'gen_cyberpunk'),
    ('s_03', 'gen_lofi'),
    ('s_04', 'gen_synthwave'),
    ('s_05', 'gen_ambient')
ON CONFLICT DO NOTHING;

-- Insert Seed Playlists
INSERT INTO playlists (id, user_id, name, description, cover_url, is_public)
VALUES
    ('pl_01', 'u_01', 'Cyberpunk Nightfall', 'Heavy bass, synth leads, and retro outrun beats for late-night city cruising.', 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500', TRUE),
    ('pl_02', 'u_01', 'Lo-Fi Chill & Code', 'Relaxing ambient beats and smooth jazzy textures to maximize your coding focus.', 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Insert Playlist Songs
INSERT INTO playlist_songs (playlist_id, song_id, position)
VALUES
    ('pl_01', 's_01', 1),
    ('pl_01', 's_02', 2),
    ('pl_01', 's_04', 3),
    ('pl_02', 's_03', 1),
    ('pl_02', 's_05', 2)
ON CONFLICT DO NOTHING;

-- Insert Seed Favorites for u_01
INSERT INTO favorites (user_id, song_id)
VALUES
    ('u_01', 's_01'),
    ('u_01', 's_03')
ON CONFLICT DO NOTHING;

import { Pool } from 'pg';
import { config } from '../config/index.js';

export interface UserRow {
  id: string;
  email: string;
  password_hash: string;
  display_name: string;
  avatar_url?: string;
  role: 'user' | 'admin' | 'moderator';
  is_guest: boolean;
  selected_genres: string[];
  status: 'active' | 'suspended' | 'deactivated';
  created_at: Date;
}

export interface SongRow {
  id: string;
  title: string;
  artist_id?: string;
  artist_name?: string;
  album_id?: string;
  album_title?: string;
  cover_url?: string;
  duration_sec: number;
  stream_url: string;
  download_url?: string;
  lyrics_lrc?: string;
  bitrate_kbps: number;
  is_lossless: boolean;
  lossless_format: string;
  play_count: number;
  is_favorite?: boolean;
}

export interface ArtistRow {
  id: string;
  name: string;
  bio: string;
  image_url: string;
  banner_url?: string;
  is_verified: boolean;
  monthly_listeners: number;
}

export interface AlbumRow {
  id: string;
  title: string;
  cover_url: string;
  release_date: string;
  artist_name?: string;
  album_type: string;
}

export interface PlaylistRow {
  id: string;
  user_id: string;
  name: string;
  description: string;
  cover_url: string;
  is_public: boolean;
  song_count?: number;
  created_at: Date;
}

// In-Memory Fallback State (initialized with seed data)
class InMemoryDatabase {
  users: UserRow[] = [
    {
      id: 'u_admin',
      email: 'admin@melodia.app',
      password_hash: '$2a$10$3p8C6K89L.6QeI4Yf4Q3heU0fO0m8vD8uL5W2h1b6v0a.G11c0Kz9', // S3cure!pass
      display_name: 'System Admin',
      avatar_url: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500',
      role: 'admin',
      is_guest: false,
      selected_genres: ['Synthwave', 'Electronic'],
      status: 'active',
      created_at: new Date(),
    },
    {
      id: 'u_01',
      email: 'linh@example.com',
      password_hash: '$2a$10$3p8C6K89L.6QeI4Yf4Q3heU0fO0m8vD8uL5W2h1b6v0a.G11c0Kz9', // S3cure!pass
      display_name: 'Linh Nguyen',
      avatar_url: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500',
      role: 'user',
      is_guest: false,
      selected_genres: ['Synthwave', 'Lo-Fi', 'Cyberpunk'],
      status: 'active',
      created_at: new Date(),
    },
    {
      id: 'u_guest',
      email: 'guest@melodia.local',
      password_hash: '$2a$10$3p8C6K89L.6QeI4Yf4Q3heU0fO0m8vD8uL5W2h1b6v0a.G11c0Kz9',
      display_name: 'Guest Explorer',
      role: 'user',
      is_guest: true,
      selected_genres: ['Synthwave'],
      status: 'active',
      created_at: new Date(),
    },
  ];

  artists: ArtistRow[] = [
    {
      id: 'art_01',
      name: 'Aurora Waves',
      bio: 'Pioneering ambient synthwave soundscapes with analog hardware and immersive binaural depth.',
      image_url: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500',
      is_verified: true,
      monthly_listeners: 1250000,
    },
    {
      id: 'art_02',
      name: 'Cyber Dreamer',
      bio: 'Futuristic synthpop combining cyberpunk themes with soaring melodic leads.',
      image_url: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500',
      is_verified: true,
      monthly_listeners: 890000,
    },
    {
      id: 'art_03',
      name: 'Luna Eclipse',
      bio: 'Cinematic chillstep, lo-fi beats, and nocturnal piano melodies.',
      image_url: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500',
      is_verified: true,
      monthly_listeners: 640000,
    },
    {
      id: 'art_04',
      name: 'Retro Boy',
      bio: 'Pure 80s nostalgia, outrun arpeggios, and vintage drum machines.',
      image_url: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500',
      is_verified: false,
      monthly_listeners: 420000,
    },
  ];

  albums: AlbumRow[] = [
    {
      id: 'alb_01',
      title: 'Midnight Odyssey',
      cover_url: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500',
      release_date: '2025-10-15',
      artist_name: 'Aurora Waves',
      album_type: 'album',
    },
    {
      id: 'alb_02',
      title: 'Cyberpunk Retrospect',
      cover_url: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500',
      release_date: '2025-11-20',
      artist_name: 'Cyber Dreamer',
      album_type: 'album',
    },
    {
      id: 'alb_03',
      title: 'Echoes of the Night',
      cover_url: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500',
      release_date: '2026-01-10',
      artist_name: 'Luna Eclipse',
      album_type: 'ep',
    },
  ];

  songs: SongRow[] = [
    {
      id: 's_01',
      title: 'Midnight City Drive',
      artist_id: 'art_01',
      artist_name: 'Aurora Waves',
      album_id: 'alb_01',
      album_title: 'Midnight Odyssey',
      cover_url: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500',
      duration_sec: 214,
      stream_url: 'https://cdn.melodia.example/stream/midnight_city_drive.flac',
      lyrics_lrc: `[00:00.00]Cruising under the neon lights\n[00:15.50]The city hums beneath the tires\n[00:32.10]Electric shadows fade to night\n[00:48.80]Chasing our midnight desires\n[01:05.20]Feel the bassline through the floor\n[01:21.00]Never wanting anything more\n[01:40.40]Synthesized dreams in purple glow\n[02:00.00]Where the endless highways flow`,
      bitrate_kbps: 1411,
      is_lossless: true,
      lossless_format: 'FLAC 24-bit / 96kHz',
      play_count: 89400,
    },
    {
      id: 's_02',
      title: 'Neon Horizon',
      artist_id: 'art_02',
      artist_name: 'Cyber Dreamer',
      album_id: 'alb_02',
      album_title: 'Cyberpunk Retrospect',
      cover_url: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500',
      duration_sec: 198,
      stream_url: 'https://cdn.melodia.example/stream/neon_horizon.flac',
      lyrics_lrc: `[00:00.00]The skyline glows in crimson red\n[00:18.00]Thoughts of the future in my head\n[00:35.00]Speeding toward the morning light\n[00:54.00]Leaving behind the quiet night`,
      bitrate_kbps: 1411,
      is_lossless: true,
      lossless_format: 'FLAC 24-bit / 96kHz',
      play_count: 67300,
    },
    {
      id: 's_03',
      title: 'Echoes in the Rain',
      artist_id: 'art_03',
      artist_name: 'Luna Eclipse',
      album_id: 'alb_03',
      album_title: 'Echoes of the Night',
      cover_url: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500',
      duration_sec: 245,
      stream_url: 'https://cdn.melodia.example/stream/echoes_in_the_rain.flac',
      lyrics_lrc: `[00:00.00]Raindrops falling on the glass\n[00:22.00]Watching all the moments pass\n[00:44.00]Soft echoes in the empty street\n[01:08.00]Where our lost memories meet`,
      bitrate_kbps: 1411,
      is_lossless: true,
      lossless_format: 'FLAC 24-bit / 192kHz',
      play_count: 45100,
    },
    {
      id: 's_04',
      title: 'Synthesized Love',
      artist_id: 'art_04',
      artist_name: 'Retro Boy',
      album_id: 'alb_01',
      album_title: 'Midnight Odyssey',
      cover_url: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500',
      duration_sec: 186,
      stream_url: 'https://cdn.melodia.example/stream/synthesized_love.flac',
      lyrics_lrc: `[00:00.00]Analog heart in a digital world\n[00:20.00]Watch the synthetic love unfurl\n[00:40.00]Binary pulses beat in time\n[01:02.00]Your frequency perfectly matches mine`,
      bitrate_kbps: 1411,
      is_lossless: true,
      lossless_format: 'FLAC 24-bit / 96kHz',
      play_count: 38200,
    },
    {
      id: 's_05',
      title: 'Starlight Serenade',
      artist_id: 'art_01',
      artist_name: 'Aurora Waves',
      album_id: 'alb_02',
      album_title: 'Cyberpunk Retrospect',
      cover_url: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500',
      duration_sec: 230,
      stream_url: 'https://cdn.melodia.example/stream/starlight_serenade.flac',
      lyrics_lrc: `[00:00.00]Stars ignite across the dark\n[00:25.00]A gentle whisper, a glowing spark\n[00:50.00]Floating free in outer space\n[01:15.00]Finding peace in this warm embrace`,
      bitrate_kbps: 1411,
      is_lossless: true,
      lossless_format: 'FLAC 24-bit / 96kHz',
      play_count: 51000,
    },
  ];

  playlists: PlaylistRow[] = [
    {
      id: 'pl_01',
      user_id: 'u_01',
      name: 'Cyberpunk Nightfall',
      description: 'Heavy bass, synth leads, and retro outrun beats for late-night city cruising.',
      cover_url: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500',
      is_public: true,
      song_count: 3,
      created_at: new Date(),
    },
    {
      id: 'pl_02',
      user_id: 'u_01',
      name: 'Lo-Fi Chill & Code',
      description: 'Relaxing ambient beats and smooth jazzy textures to maximize your coding focus.',
      cover_url: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500',
      is_public: true,
      song_count: 2,
      created_at: new Date(),
    },
  ];

  playlistSongs: { playlist_id: string; song_id: string; position: number }[] = [
    { playlist_id: 'pl_01', song_id: 's_01', position: 1 },
    { playlist_id: 'pl_01', song_id: 's_02', position: 2 },
    { playlist_id: 'pl_01', song_id: 's_04', position: 3 },
    { playlist_id: 'pl_02', song_id: 's_03', position: 1 },
    { playlist_id: 'pl_02', song_id: 's_05', position: 2 },
  ];

  favorites: { user_id: string; song_id: string; created_at: Date }[] = [
    { user_id: 'u_01', song_id: 's_01', created_at: new Date() },
    { user_id: 'u_01', song_id: 's_03', created_at: new Date() },
  ];

  history: { id: number; user_id: string; song_id: string; duration_played_sec: number; completed: boolean; played_at: Date }[] = [];
  downloads: { id: string; user_id: string; song_id: string; license_token: string; expires_at: Date }[] = [];
}

export const inMemoryDb = new InMemoryDatabase();

let pgPool: Pool | null = null;
let isPgConnected = false;

export async function initDatabase(): Promise<boolean> {
  try {
    pgPool = new Pool({
      connectionString: config.database.url,
      connectionTimeoutMillis: 2000,
    });
    const client = await pgPool.connect();
    client.release();
    isPgConnected = true;
    console.log('✅ Connected to PostgreSQL database successfully.');
    return true;
  } catch (err) {
    isPgConnected = false;
    console.warn('⚠️ PostgreSQL unavailable. Falling back to High-Fidelity In-Memory Data Store.');
    return false;
  }
}

export function isUsingPostgres(): boolean {
  return isPgConnected;
}

export { pgPool };

export const swaggerDocument = {
  openapi: '3.0.3',
  info: {
    title: 'Melodia Music Streaming REST API',
    version: '1.0.0',
    description: `High-Fidelity REST Backend API for **Melodia - Lossless Music Streaming Application**
Developed for **HCMUT Mobile Application Development (CO3043) Assignment 1**
Group CC01 - Group 14

### Features:
- 🎵 **Hi-Res Lossless Audio Streaming** (24-bit/96kHz FLAC & 320kbps MP3)
- 🎤 **Synchronized LRC Lyrics** with millisecond timestamps
- 🔐 **Dual-Token JWT Authentication** (15-min Access Token, 7-day Refresh Token)
- 👤 **Guest Preview Mode** with 30-second streaming limit
- 💾 **Offline Caching & AES-256 Download Licensing**
- 🎧 **Sonic Chemistry Matching (98%)** & Personalized Recommendations
- 📑 **Complete 17-Entity Relational Data Model** (PostgreSQL)
- ⚡ **Instant In-Memory Fallback** for zero-dependency local testing`,
    contact: {
      name: 'Melodia Engineering Team (HCMUT CC01)',
      email: 'support@melodia.example',
    },
  },
  servers: [
    {
      url: 'http://localhost:3000',
      description: 'Local Development Server',
    },
    {
      url: 'http://10.0.2.2:3000',
      description: 'Android Emulator Host Gateway',
    },
  ],
  components: {
    securitySchemes: {
      BearerAuth: {
        type: 'http',
        scheme: 'bearer',
        bearerFormat: 'JWT',
        description: 'Provide your JWT access token as \`Bearer <token>\`',
      },
    },
    schemas: {
      ErrorResponse: {
        type: 'object',
        properties: {
          error: {
            type: 'object',
            properties: {
              code: { type: 'string', example: 'VALIDATION_FAILED' },
              message: { type: 'string', example: 'Invalid email or password' },
              details: { type: 'array', items: { type: 'object' } },
              requestId: { type: 'string', example: 'req_8f1a3b_1740000000' },
            },
          },
        },
      },
      Song: {
        type: 'object',
        properties: {
          id: { type: 'string', example: 's_01' },
          title: { type: 'string', example: 'Cyberpunk Odyssey' },
          artist_id: { type: 'string', example: 'art_01' },
          artist_name: { type: 'string', example: 'Neon Mirage' },
          album_id: { type: 'string', example: 'alb_01' },
          album_title: { type: 'string', example: 'Neon Genesis 2099' },
          cover_url: { type: 'string', example: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500' },
          duration_sec: { type: 'integer', example: 218 },
          stream_url: { type: 'string', example: 'https://assets.melodia.example/audio/cyberpunk_odyssey_96k.flac' },
          download_url: { type: 'string', example: 'https://assets.melodia.example/audio/cyberpunk_odyssey_96k.flac' },
          bitrate_kbps: { type: 'integer', example: 9216 },
          is_lossless: { type: 'boolean', example: true },
          lossless_format: { type: 'string', example: 'FLAC 24-bit / 96kHz' },
          play_count: { type: 'integer', example: 124500 },
          is_favorite: { type: 'boolean', example: true },
        },
      },
      Artist: {
        type: 'object',
        properties: {
          id: { type: 'string', example: 'art_01' },
          name: { type: 'string', example: 'Neon Mirage' },
          bio: { type: 'string', example: 'Pioneer of high-octane synthwave and electronic soundscapes.' },
          image_url: { type: 'string', example: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500' },
          banner_url: { type: 'string', example: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1200' },
          is_verified: { type: 'boolean', example: true },
          monthly_listeners: { type: 'integer', example: 1450200 },
        },
      },
      Album: {
        type: 'object',
        properties: {
          id: { type: 'string', example: 'alb_01' },
          title: { type: 'string', example: 'Neon Genesis 2099' },
          cover_url: { type: 'string', example: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500' },
          release_date: { type: 'string', example: '2025-11-15' },
          artist_name: { type: 'string', example: 'Neon Mirage' },
          album_type: { type: 'string', example: 'album' },
        },
      },
      Playlist: {
        type: 'object',
        properties: {
          id: { type: 'string', example: 'pl_01' },
          name: { type: 'string', example: 'Cyberpunk Nightfall' },
          description: { type: 'string', example: 'Heavy bass, synth leads, and retro outrun beats.' },
          cover_url: { type: 'string', example: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500' },
          is_public: { type: 'boolean', example: true },
          song_count: { type: 'integer', example: 3 },
        },
      },
    },
  },
  paths: {
    '/v1/auth/register': {
      post: {
        tags: ['Authentication'],
        summary: 'Register new user account',
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                required: ['email', 'password', 'displayName'],
                properties: {
                  email: { type: 'string', format: 'email', example: 'alex@melodia.io' },
                  password: { type: 'string', format: 'password', example: 'Password123!' },
                  displayName: { type: 'string', example: 'Alex Mercer' },
                  selectedGenres: { type: 'array', items: { type: 'string' }, example: ['Synthwave', 'Cyberpunk'] },
                },
              },
            },
          },
        },
        responses: {
          '201': {
            description: 'User registered successfully',
            content: {
              'application/json': {
                schema: {
                  type: 'object',
                  properties: {
                    user: { type: 'object' },
                    accessToken: { type: 'string' },
                    refreshToken: { type: 'string' },
                    expiresIn: { type: 'integer', example: 900 },
                  },
                },
              },
            },
          },
          '409': { description: 'Email already registered' },
        },
      },
    },
    '/v1/auth/login': {
      post: {
        tags: ['Authentication'],
        summary: 'Log in with credentials or Guest mode',
        requestBody: {
          required: false,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                properties: {
                  email: { type: 'string', example: 'alex@melodia.io' },
                  password: { type: 'string', example: 'Password123!' },
                  isGuest: { type: 'boolean', example: false },
                },
              },
            },
          },
        },
        responses: {
          '200': { description: 'Logged in successfully' },
          '401': { description: 'Invalid email or password' },
        },
      },
    },
    '/v1/auth/refresh': {
      post: {
        tags: ['Authentication'],
        summary: 'Refresh expired access token',
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                required: ['refreshToken'],
                properties: {
                  refreshToken: { type: 'string' },
                },
              },
            },
          },
        },
        responses: {
          '200': { description: 'Tokens refreshed successfully' },
          '401': { description: 'Invalid or expired refresh token' },
        },
      },
    },
    '/v1/auth/logout': {
      post: {
        tags: ['Authentication'],
        summary: 'Log out current session',
        security: [{ BearerAuth: [] }],
        responses: {
          '200': { description: 'Logged out successfully' },
        },
      },
    },
    '/v1/home': {
      get: {
        tags: ['Discovery'],
        summary: 'Get personalized home screen data',
        security: [{ BearerAuth: [] }],
        responses: {
          '200': {
            description: 'Home discovery feed with Sonic Chemistry, quick picks, and mixes',
          },
        },
      },
    },
    '/v1/search': {
      get: {
        tags: ['Discovery'],
        summary: 'Unified search across songs, artists, albums, and playlists',
        parameters: [
          { name: 'q', in: 'query', schema: { type: 'string' }, description: 'Search keywords' },
          { name: 'type', in: 'query', schema: { type: 'string', enum: ['all', 'song', 'artist', 'album', 'playlist'] } },
          { name: 'page', in: 'query', schema: { type: 'integer', default: 1 } },
          { name: 'limit', in: 'query', schema: { type: 'integer', default: 20 } },
        ],
        responses: {
          '200': { description: 'Search results' },
        },
      },
    },
    '/v1/songs/{id}': {
      get: {
        tags: ['Catalog'],
        summary: 'Get song details and metadata',
        parameters: [{ name: 'id', in: 'path', required: true, schema: { type: 'string' } }],
        responses: {
          '200': { description: 'Song metadata' },
          '404': { description: 'Song not found' },
        },
      },
    },
    '/v1/songs/{id}/stream': {
      get: {
        tags: ['Catalog'],
        summary: 'Get streaming URL (Lossless 24-bit or 30s Guest Preview)',
        parameters: [
          { name: 'id', in: 'path', required: true, schema: { type: 'string' } },
          { name: 'quality', in: 'query', schema: { type: 'string', enum: ['128', '320', 'lossless'] } },
        ],
        responses: {
          '200': { description: 'Streaming audio URL metadata' },
        },
      },
    },
    '/v1/songs/{id}/lyrics': {
      get: {
        tags: ['Catalog'],
        summary: 'Get synchronized LRC lyrics with timestamps',
        parameters: [{ name: 'id', in: 'path', required: true, schema: { type: 'string' } }],
        responses: {
          '200': { description: 'Synchronized lyrics with parsed lines' },
        },
      },
    },
    '/v1/artists/{id}': {
      get: {
        tags: ['Catalog'],
        summary: 'Get artist profile, top tracks, and discography',
        parameters: [{ name: 'id', in: 'path', required: true, schema: { type: 'string' } }],
        responses: {
          '200': { description: 'Artist details' },
        },
      },
    },
    '/v1/albums/{id}': {
      get: {
        tags: ['Catalog'],
        summary: 'Get album details and tracklist',
        parameters: [{ name: 'id', in: 'path', required: true, schema: { type: 'string' } }],
        responses: {
          '200': { description: 'Album tracklist' },
        },
      },
    },
    '/v1/playlists': {
      post: {
        tags: ['Playlists'],
        summary: 'Create a new user playlist',
        security: [{ BearerAuth: [] }],
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                required: ['name'],
                properties: {
                  name: { type: 'string', example: 'Midnight Drive' },
                  description: { type: 'string', example: 'Atmospheric electronic sounds' },
                  coverUrl: { type: 'string' },
                  isPublic: { type: 'boolean', default: true },
                },
              },
            },
          },
        },
        responses: {
          '201': { description: 'Playlist created' },
        },
      },
    },
    '/v1/playlists/{id}': {
      get: {
        tags: ['Playlists'],
        summary: 'Get playlist details and songs',
        parameters: [{ name: 'id', in: 'path', required: true, schema: { type: 'string' } }],
        responses: { '200': { description: 'Playlist details' } },
      },
      patch: {
        tags: ['Playlists'],
        summary: 'Update playlist metadata',
        security: [{ BearerAuth: [] }],
        parameters: [{ name: 'id', in: 'path', required: true, schema: { type: 'string' } }],
        responses: { '200': { description: 'Playlist updated' } },
      },
      delete: {
        tags: ['Playlists'],
        summary: 'Delete playlist',
        security: [{ BearerAuth: [] }],
        parameters: [{ name: 'id', in: 'path', required: true, schema: { type: 'string' } }],
        responses: { '204': { description: 'Playlist deleted' } },
      },
    },
    '/v1/playlists/{id}/songs': {
      post: {
        tags: ['Playlists'],
        summary: 'Add song to playlist',
        security: [{ BearerAuth: [] }],
        parameters: [{ name: 'id', in: 'path', required: true, schema: { type: 'string' } }],
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                required: ['songId'],
                properties: { songId: { type: 'string', example: 's_01' } },
              },
            },
          },
        },
        responses: {
          '201': { description: 'Song added to playlist' },
          '409': { description: 'Song already in playlist' },
        },
      },
    },
    '/v1/playlists/{id}/songs/{songId}': {
      delete: {
        tags: ['Playlists'],
        summary: 'Remove song from playlist',
        security: [{ BearerAuth: [] }],
        parameters: [
          { name: 'id', in: 'path', required: true, schema: { type: 'string' } },
          { name: 'songId', in: 'path', required: true, schema: { type: 'string' } },
        ],
        responses: { '204': { description: 'Song removed' } },
      },
    },
    '/v1/me/playlists': {
      get: {
        tags: ['User Library'],
        summary: 'Get current user playlists',
        security: [{ BearerAuth: [] }],
        responses: { '200': { description: 'List of playlists' } },
      },
    },
    '/v1/me/favorites': {
      get: {
        tags: ['User Library'],
        summary: 'Get current user favorite songs',
        security: [{ BearerAuth: [] }],
        responses: { '200': { description: 'List of favorite songs' } },
      },
    },
    '/v1/me/favorites/{songId}': {
      put: {
        tags: ['User Library'],
        summary: 'Like song (add to favorites)',
        security: [{ BearerAuth: [] }],
        parameters: [{ name: 'songId', in: 'path', required: true, schema: { type: 'string' } }],
        responses: { '204': { description: 'Marked as favorite' } },
      },
      delete: {
        tags: ['User Library'],
        summary: 'Unlike song (remove from favorites)',
        security: [{ BearerAuth: [] }],
        parameters: [{ name: 'songId', in: 'path', required: true, schema: { type: 'string' } }],
        responses: { '204': { description: 'Removed from favorites' } },
      },
    },
    '/v1/me/history': {
      get: {
        tags: ['User Library'],
        summary: 'Get recently played songs',
        security: [{ BearerAuth: [] }],
        responses: { '200': { description: 'Play history items' } },
      },
      post: {
        tags: ['User Library'],
        summary: 'Record song playback event',
        security: [{ BearerAuth: [] }],
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                required: ['songId'],
                properties: {
                  songId: { type: 'string', example: 's_01' },
                  durationPlayedSec: { type: 'integer', example: 120 },
                  completed: { type: 'boolean', example: true },
                },
              },
            },
          },
        },
        responses: { '201': { description: 'Play history recorded' } },
      },
    },
    '/v1/downloads': {
      post: {
        tags: ['Downloads & Offline'],
        summary: 'Request AES-256 encrypted offline download license token',
        security: [{ BearerAuth: [] }],
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                required: ['songId'],
                properties: {
                  songId: { type: 'string', example: 's_01' },
                  deviceId: { type: 'string', example: 'dev_pixel9_01' },
                },
              },
            },
          },
        },
        responses: {
          '201': {
            description: 'Download license granted',
            content: {
              'application/json': {
                schema: {
                  type: 'object',
                  properties: {
                    songId: { type: 'string' },
                    downloadUrl: { type: 'string' },
                    licenseToken: { type: 'string' },
                    encryptionAlgorithm: { type: 'string', example: 'AES-256-GCM' },
                    expiresAt: { type: 'string' },
                  },
                },
              },
            },
          },
          '403': { description: 'Guests cannot download songs' },
        },
      },
    },
  },
};

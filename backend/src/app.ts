import express, { Express, Request, Response, NextFunction } from 'express';
import cors from 'cors';
import swaggerUi from 'swagger-ui-express';
import { requestIdMiddleware, errorMiddleware } from './middlewares/error.middleware.js';
import { swaggerDocument } from './docs/swagger.js';

// Routers
import { authRouter } from './modules/auth/auth.router.js';
import { homeRouter } from './modules/home/home.router.js';
import { songsRouter } from './modules/songs/songs.router.js';
import { searchRouter } from './modules/search/search.router.js';
import { artistsRouter, albumsRouter } from './modules/artists/artists.router.js';
import { playlistsRouter } from './modules/playlists/playlists.router.js';
import { libraryRouter } from './modules/library/library.router.js';
import { authMiddleware } from './middlewares/auth.middleware.js';
import { inMemoryDb } from './database/index.js';

export function createApp(): Express {
  const app = express();

  // Basic security and parsing middlewares
  app.use(cors());
  app.use(express.json());
  app.use(express.urlencoded({ extended: true }));
  app.use(requestIdMiddleware);

  // Health check endpoint
  app.get('/health', (req: Request, res: Response) => {
    res.status(200).json({
      status: 'UP',
      uptime: process.uptime(),
      timestamp: new Date().toISOString(),
      service: 'melodia-rest-backend',
      version: '1.0.0',
    });
  });

  // Swagger Documentation
  app.use('/v1/docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));
  app.get('/docs', (req: Request, res: Response) => {
    res.redirect('/v1/docs');
  });

  // REST API Routes (/v1)
  app.use('/v1/auth', authRouter);
  app.use('/v1/home', homeRouter);
  app.use('/v1/songs', songsRouter);
  app.use('/v1/search', searchRouter);
  app.use('/v1/artists', artistsRouter);
  app.use('/v1/albums', albumsRouter);
  app.use('/v1/playlists', playlistsRouter);

  // Library & User Profile routes
  app.use('/v1/me', libraryRouter);

  // Also support /v1/me/playlists per Section 5.3 contract
  app.get('/v1/me/playlists', authMiddleware, (req: Request, res: Response) => {
    const userId = req.user!.id;
    const userPlaylists = inMemoryDb.playlists.filter((p) => p.user_id === userId);
    const decorated = userPlaylists.map((pl) => {
      const songsInPl = inMemoryDb.playlistSongs.filter((ps) => ps.playlist_id === pl.id);
      return {
        ...pl,
        song_count: songsInPl.length,
      };
    });
    res.status(200).json(decorated);
  });

  // Support /v1/downloads per Section 5.3 contract (forward to library download logic)
  app.post('/v1/downloads', authMiddleware, async (req: Request, res: Response, next: NextFunction) => {
    try {
      const userId = req.user!.id;
      const { songId } = req.body;
      if (!songId) {
        res.status(400).json({
          error: {
            code: 'VALIDATION_FAILED',
            message: 'songId is required.',
            details: [],
            requestId: req.requestId,
          },
        });
        return;
      }
      const song = inMemoryDb.songs.find((s) => s.id === songId);
      if (!song) {
        res.status(404).json({
          error: {
            code: 'SONG_NOT_FOUND',
            message: 'Song does not exist.',
            details: [],
            requestId: req.requestId,
          },
        });
        return;
      }
      if (req.user!.isGuest) {
        res.status(403).json({
          error: {
            code: 'DOWNLOAD_NOT_ALLOWED',
            message: 'Offline downloading requires a registered account.',
            details: [],
            requestId: req.requestId,
          },
        });
        return;
      }
      const downloadToken = `dl_lic_${Buffer.from(`${userId}:${songId}:${Date.now()}`).toString('base64url')}`;
      const expiresAt = new Date(Date.now() + 30 * 24 * 3600 * 1000);
      res.status(201).json({
        songId: song.id,
        title: song.title,
        artist: song.artist_name,
        downloadUrl: song.stream_url,
        bitrateKbps: song.bitrate_kbps,
        licenseToken: downloadToken,
        encryptionAlgorithm: 'AES-256-GCM',
        hardwareKeyStoreProtected: true,
        expiresAt: expiresAt.toISOString(),
      });
    } catch (err) {
      next(err);
    }
  });

  // 404 Unified Error Handler
  app.use((req: Request, res: Response) => {
    res.status(404).json({
      error: {
        code: 'ROUTE_NOT_FOUND',
        message: `Endpoint ${req.method} ${req.originalUrl} not found.`,
        details: [],
        requestId: req.requestId || `req_${Date.now().toString(36)}`,
      },
    });
  });

  // Global Error Handler
  app.use(errorMiddleware);

  return app;
}

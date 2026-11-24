import { Router, Request, Response } from 'express';
import { inMemoryDb } from '../../database/index.js';
import { AppError } from '../../middlewares/error.middleware.js';
import { authMiddleware } from '../../middlewares/auth.middleware.js';

export const libraryRouter = Router();

// GET /v1/me/favorites
libraryRouter.get('/favorites', authMiddleware, async (req: Request, res: Response) => {
  const userId = req.user!.id;
  const favoriteEntries = inMemoryDb.favorites.filter((f) => f.user_id === userId);

  const songs = favoriteEntries
    .map((f) => {
      const song = inMemoryDb.songs.find((s) => s.id === f.song_id);
      return song ? { ...song, is_favorite: true, likedAt: f.created_at } : null;
    })
    .filter(Boolean);

  res.status(200).json({
    total: songs.length,
    items: songs,
  });
});

// PUT /v1/me/favorites/:songId (Like song)
libraryRouter.put('/favorites/:songId', authMiddleware, async (req: Request, res: Response) => {
  const userId = req.user!.id;
  const songId = req.params.songId;

  const song = inMemoryDb.songs.find((s) => s.id === songId);
  if (!song) {
    throw new AppError(404, 'SONG_NOT_FOUND', 'Song does not exist.');
  }

  const exists = inMemoryDb.favorites.some((f) => f.user_id === userId && f.song_id === songId);
  if (!exists) {
    inMemoryDb.favorites.push({
      user_id: userId,
      song_id: songId,
      created_at: new Date(),
    });
  }

  res.status(204).send();
});

// DELETE /v1/me/favorites/:songId (Unlike song)
libraryRouter.delete('/favorites/:songId', authMiddleware, async (req: Request, res: Response) => {
  const userId = req.user!.id;
  const songId = req.params.songId;

  inMemoryDb.favorites = inMemoryDb.favorites.filter(
    (f) => !(f.user_id === userId && f.song_id === songId)
  );

  res.status(204).send();
});

// GET /v1/me/history
libraryRouter.get('/history', authMiddleware, async (req: Request, res: Response) => {
  const userId = req.user!.id;
  const userHistory = inMemoryDb.history
    .filter((h) => h.user_id === userId)
    .sort((a, b) => b.played_at.getTime() - a.played_at.getTime());

  const items = userHistory.map((h) => {
    const song = inMemoryDb.songs.find((s) => s.id === h.song_id);
    return {
      historyId: h.id,
      song,
      durationPlayedSec: h.duration_played_sec,
      completed: h.completed,
      playedAt: h.played_at,
    };
  });

  res.status(200).json({
    total: items.length,
    items,
  });
});

// POST /v1/me/history
libraryRouter.post('/history', authMiddleware, async (req: Request, res: Response) => {
  const userId = req.user!.id;
  const { songId, durationPlayedSec, completed } = req.body;

  if (!songId) {
    throw new AppError(400, 'VALIDATION_FAILED', 'songId is required.');
  }

  const song = inMemoryDb.songs.find((s) => s.id === songId);
  if (!song) {
    throw new AppError(404, 'SONG_NOT_FOUND', 'Song does not exist.');
  }

  // Increment song play count
  song.play_count += 1;

  inMemoryDb.history.push({
    id: Date.now(),
    user_id: userId,
    song_id: songId,
    duration_played_sec: parseInt(durationPlayedSec || '0', 10),
    completed: Boolean(completed),
    played_at: new Date(),
  });

  res.status(201).json({ message: 'Play history recorded.' });
});

// POST /v1/downloads (Request download license)
libraryRouter.post('/downloads', authMiddleware, async (req: Request, res: Response) => {
  const userId = req.user!.id;
  const { songId, deviceId } = req.body;

  if (!songId) {
    throw new AppError(400, 'VALIDATION_FAILED', 'songId is required.');
  }

  const song = inMemoryDb.songs.find((s) => s.id === songId);
  if (!song) {
    throw new AppError(404, 'SONG_NOT_FOUND', 'Song does not exist.');
  }

  // Guest users cannot download offline songs (requires registered account)
  if (req.user!.isGuest) {
    throw new AppError(403, 'DOWNLOAD_NOT_ALLOWED', 'Offline downloading requires a registered account.');
  }

  const downloadToken = `dl_lic_${Buffer.from(`${userId}:${songId}:${Date.now()}`).toString('base64url')}`;
  const expiresAt = new Date(Date.now() + 30 * 24 * 3600 * 1000); // 30-day offline license

  inMemoryDb.downloads.push({
    id: `dl_${Date.now().toString(36)}`,
    user_id: userId,
    song_id: songId,
    license_token: downloadToken,
    expires_at: expiresAt,
  });

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
});

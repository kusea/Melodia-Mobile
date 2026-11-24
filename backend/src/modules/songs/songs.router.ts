import { Router, Request, Response } from 'express';
import { inMemoryDb } from '../../database/index.js';
import { AppError } from '../../middlewares/error.middleware.js';
import { optionalAuthMiddleware, authMiddleware } from '../../middlewares/auth.middleware.js';

export const songsRouter = Router();

// GET /v1/songs/:id
songsRouter.get('/:id', optionalAuthMiddleware, async (req: Request, res: Response) => {
  const song = inMemoryDb.songs.find((s) => s.id === req.params.id);
  if (!song) {
    throw new AppError(404, 'SONG_NOT_FOUND', 'Requested song does not exist.');
  }

  const isFavorite = req.user
    ? inMemoryDb.favorites.some((f) => f.user_id === req.user?.id && f.song_id === song.id)
    : false;

  res.status(200).json({
    ...song,
    is_favorite: isFavorite,
  });
});

// GET /v1/songs/:id/stream?quality=
songsRouter.get('/:id/stream', optionalAuthMiddleware, async (req: Request, res: Response) => {
  const song = inMemoryDb.songs.find((s) => s.id === req.params.id);
  if (!song) {
    throw new AppError(404, 'SONG_NOT_FOUND', 'Requested song does not exist.');
  }

  const quality = req.query.quality as string || '320';
  const isGuest = !req.user || req.user.isGuest;

  // Guest users are restricted to 30s preview limit (Requirement from PDF Page 40)
  const previewLimitSec = isGuest ? 30 : null;
  const expiresAt = new Date(Date.now() + 3600 * 1000).toISOString();

  res.status(200).json({
    songId: song.id,
    url: song.stream_url,
    quality,
    bitrateKbps: song.bitrate_kbps,
    isLossless: !isGuest && song.is_lossless,
    losslessFormat: isGuest ? 'AAC 128kbps (Preview)' : song.lossless_format,
    previewLimitSec,
    expiresAt,
  });
});

// GET /v1/songs/:id/lyrics
songsRouter.get('/:id/lyrics', optionalAuthMiddleware, async (req: Request, res: Response) => {
  const song = inMemoryDb.songs.find((s) => s.id === req.params.id);
  if (!song) {
    throw new AppError(404, 'SONG_NOT_FOUND', 'Requested song does not exist.');
  }

  const lrc = song.lyrics_lrc || `[00:00.00]Instrumental intro\n[00:20.00]${song.title} playing in Hi-Res Lossless`;

  // Parse LRC into structured lines with timestamp in milliseconds
  const lines = lrc.split('\n').filter((l) => l.trim().length > 0).map((line) => {
    const match = line.match(/\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)/);
    if (match) {
      const minutes = parseInt(match[1], 10);
      const seconds = parseInt(match[2], 10);
      const millis = parseInt(match[3].padEnd(3, '0'), 10);
      const timestampMs = minutes * 60000 + seconds * 1000 + millis;
      return { timestampMs, text: match[4].trim() };
    }
    return { timestampMs: 0, text: line };
  });

  res.status(200).json({
    songId: song.id,
    title: song.title,
    artist: song.artist_name,
    lyricsLrc: lrc,
    lines,
  });
});

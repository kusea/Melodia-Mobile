import { Router, Request, Response } from 'express';
import { inMemoryDb } from '../../database/index.js';
import { AppError } from '../../middlewares/error.middleware.js';
import { authMiddleware, optionalAuthMiddleware } from '../../middlewares/auth.middleware.js';

export const playlistsRouter = Router();

// GET /v1/me/playlists
playlistsRouter.get('/me', authMiddleware, async (req: Request, res: Response) => {
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

// GET /v1/playlists/:id
playlistsRouter.get('/:id', optionalAuthMiddleware, async (req: Request, res: Response) => {
  const playlist = inMemoryDb.playlists.find((p) => p.id === req.params.id);
  if (!playlist) {
    throw new AppError(404, 'PLAYLIST_NOT_FOUND', 'Playlist not found.');
  }

  const playlistSongEntries = inMemoryDb.playlistSongs
    .filter((ps) => ps.playlist_id === playlist.id)
    .sort((a, b) => a.position - b.position);

  const songs = playlistSongEntries
    .map((ps) => inMemoryDb.songs.find((s) => s.id === ps.song_id))
    .filter(Boolean);

  res.status(200).json({
    ...playlist,
    song_count: songs.length,
    songs,
  });
});

// POST /v1/playlists
playlistsRouter.post('/', authMiddleware, async (req: Request, res: Response) => {
  const { name, description, coverUrl, isPublic } = req.body;
  if (!name || name.trim().length === 0) {
    throw new AppError(400, 'VALIDATION_FAILED', 'Playlist name is required.');
  }

  const newPlaylist = {
    id: `pl_${Date.now().toString(36)}`,
    user_id: req.user!.id,
    name: name.trim(),
    description: description || '',
    cover_url: coverUrl || 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500',
    is_public: isPublic !== undefined ? Boolean(isPublic) : true,
    song_count: 0,
    created_at: new Date(),
  };

  inMemoryDb.playlists.push(newPlaylist);
  res.status(201).json(newPlaylist);
});

// PATCH /v1/playlists/:id
playlistsRouter.patch('/:id', authMiddleware, async (req: Request, res: Response) => {
  const playlist = inMemoryDb.playlists.find((p) => p.id === req.params.id);
  if (!playlist) {
    throw new AppError(404, 'PLAYLIST_NOT_FOUND', 'Playlist not found.');
  }

  if (playlist.user_id !== req.user!.id && req.user!.role !== 'admin') {
    throw new AppError(403, 'FORBIDDEN', 'You do not have permission to modify this playlist.');
  }

  const { name, description, coverUrl, isPublic } = req.body;
  if (name !== undefined) playlist.name = name.trim();
  if (description !== undefined) playlist.description = description;
  if (coverUrl !== undefined) playlist.cover_url = coverUrl;
  if (isPublic !== undefined) playlist.is_public = Boolean(isPublic);

  res.status(200).json(playlist);
});

// DELETE /v1/playlists/:id
playlistsRouter.delete('/:id', authMiddleware, async (req: Request, res: Response) => {
  const index = inMemoryDb.playlists.findIndex((p) => p.id === req.params.id);
  if (index === -1) {
    throw new AppError(404, 'PLAYLIST_NOT_FOUND', 'Playlist not found.');
  }

  const playlist = inMemoryDb.playlists[index];
  if (playlist.user_id !== req.user!.id && req.user!.role !== 'admin') {
    throw new AppError(403, 'FORBIDDEN', 'You do not have permission to delete this playlist.');
  }

  inMemoryDb.playlists.splice(index, 1);
  // Remove associated playlist songs
  inMemoryDb.playlistSongs = inMemoryDb.playlistSongs.filter((ps) => ps.playlist_id !== playlist.id);

  res.status(204).send();
});

// POST /v1/playlists/:id/songs (Add song - 409 if duplicate per Page 39)
playlistsRouter.post('/:id/songs', authMiddleware, async (req: Request, res: Response) => {
  const playlist = inMemoryDb.playlists.find((p) => p.id === req.params.id);
  if (!playlist) {
    throw new AppError(404, 'PLAYLIST_NOT_FOUND', 'Playlist not found.');
  }

  if (playlist.user_id !== req.user!.id && req.user!.role !== 'admin') {
    throw new AppError(403, 'FORBIDDEN', 'You do not have permission to add songs to this playlist.');
  }

  const { songId } = req.body;
  if (!songId) {
    throw new AppError(400, 'VALIDATION_FAILED', 'songId is required.');
  }

  const song = inMemoryDb.songs.find((s) => s.id === songId);
  if (!song) {
    throw new AppError(404, 'SONG_NOT_FOUND', 'Song does not exist.');
  }

  const isDuplicate = inMemoryDb.playlistSongs.some(
    (ps) => ps.playlist_id === playlist.id && ps.song_id === songId
  );
  if (isDuplicate) {
    throw new AppError(409, 'PLAYLIST_SONG_DUPLICATE', 'Song is already in this playlist.', [
      { field: 'songId', issue: 'duplicate' },
    ]);
  }

  const currentCount = inMemoryDb.playlistSongs.filter((ps) => ps.playlist_id === playlist.id).length;
  inMemoryDb.playlistSongs.push({
    playlist_id: playlist.id,
    song_id: songId,
    position: currentCount + 1,
  });

  res.status(201).json({
    message: 'Song added to playlist successfully.',
    playlistId: playlist.id,
    songId,
    position: currentCount + 1,
  });
});

// DELETE /v1/playlists/:id/songs/:songId
playlistsRouter.delete('/:id/songs/:songId', authMiddleware, async (req: Request, res: Response) => {
  const playlist = inMemoryDb.playlists.find((p) => p.id === req.params.id);
  if (!playlist) {
    throw new AppError(404, 'PLAYLIST_NOT_FOUND', 'Playlist not found.');
  }

  if (playlist.user_id !== req.user!.id && req.user!.role !== 'admin') {
    throw new AppError(403, 'FORBIDDEN', 'You do not have permission to modify this playlist.');
  }

  const index = inMemoryDb.playlistSongs.findIndex(
    (ps) => ps.playlist_id === playlist.id && ps.song_id === req.params.songId
  );
  if (index === -1) {
    throw new AppError(404, 'SONG_NOT_IN_PLAYLIST', 'Song was not found in this playlist.');
  }

  inMemoryDb.playlistSongs.splice(index, 1);
  res.status(204).send();
});

import { Router, Request, Response } from 'express';
import { inMemoryDb } from '../../database/index.js';
import { AppError } from '../../middlewares/error.middleware.js';
import { optionalAuthMiddleware } from '../../middlewares/auth.middleware.js';

export const artistsRouter = Router();
export const albumsRouter = Router();

// GET /v1/artists/:id
artistsRouter.get('/:id', optionalAuthMiddleware, async (req: Request, res: Response) => {
  const artist = inMemoryDb.artists.find((a) => a.id === req.params.id);
  if (!artist) {
    throw new AppError(404, 'ARTIST_NOT_FOUND', 'Requested artist does not exist.');
  }

  const topSongs = inMemoryDb.songs.filter((s) => s.artist_id === artist.id);
  const albums = inMemoryDb.albums.filter((alb) => alb.artist_name === artist.name);

  res.status(200).json({
    ...artist,
    topSongs,
    albums,
  });
});

// GET /v1/albums/:id
albumsRouter.get('/:id', optionalAuthMiddleware, async (req: Request, res: Response) => {
  const album = inMemoryDb.albums.find((alb) => alb.id === req.params.id);
  if (!album) {
    throw new AppError(404, 'ALBUM_NOT_FOUND', 'Requested album does not exist.');
  }

  const songs = inMemoryDb.songs.filter((s) => s.album_id === album.id);

  res.status(200).json({
    ...album,
    songs,
    totalDurationSec: songs.reduce((sum, s) => sum + s.duration_sec, 0),
  });
});

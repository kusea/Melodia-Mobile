import { Router, Request, Response } from 'express';
import { inMemoryDb } from '../../database/index.js';
import { optionalAuthMiddleware } from '../../middlewares/auth.middleware.js';

export const searchRouter = Router();

// GET /v1/search?q=&type=&page=&limit=
searchRouter.get('/', optionalAuthMiddleware, async (req: Request, res: Response) => {
  const query = ((req.query.q as string) || '').trim().toLowerCase();
  const type = ((req.query.type as string) || 'all').toLowerCase();
  const page = Math.max(1, parseInt((req.query.page as string) || '1', 10));
  const limit = Math.max(1, Math.min(50, parseInt((req.query.limit as string) || '20', 10)));

  const matchedSongs = inMemoryDb.songs.filter(
    (s) => s.title.toLowerCase().includes(query) || (s.artist_name && s.artist_name.toLowerCase().includes(query))
  );

  const matchedArtists = inMemoryDb.artists.filter((a) => a.name.toLowerCase().includes(query));

  const matchedAlbums = inMemoryDb.albums.filter((alb) => alb.title.toLowerCase().includes(query));

  const matchedPlaylists = inMemoryDb.playlists.filter(
    (p) => p.name.toLowerCase().includes(query) || p.description.toLowerCase().includes(query)
  );

  let items: any[] = [];
  if (type === 'song') {
    items = matchedSongs;
  } else if (type === 'artist') {
    items = matchedArtists;
  } else if (type === 'album') {
    items = matchedAlbums;
  } else if (type === 'playlist') {
    items = matchedPlaylists;
  } else {
    // 'all' aggregates
    items = [
      ...matchedSongs.map((s) => ({ ...s, itemType: 'song' })),
      ...matchedArtists.map((a) => ({ ...a, itemType: 'artist' })),
      ...matchedAlbums.map((alb) => ({ ...alb, itemType: 'album' })),
      ...matchedPlaylists.map((p) => ({ ...p, itemType: 'playlist' })),
    ];
  }

  const total = items.length;
  const startIndex = (page - 1) * limit;
  const paginatedItems = items.slice(startIndex, startIndex + limit);

  res.status(200).json({
    page,
    limit,
    total,
    query,
    type,
    items: paginatedItems,
    bestMatch: matchedSongs.length > 0 ? { ...matchedSongs[0], itemType: 'song' } : null,
  });
});

import { Router, Request, Response } from 'express';
import { inMemoryDb } from '../../database/index.js';
import { optionalAuthMiddleware } from '../../middlewares/auth.middleware.js';

export const homeRouter = Router();

// GET /v1/home
homeRouter.get('/', optionalAuthMiddleware, async (req: Request, res: Response) => {
  const songs = inMemoryDb.songs;
  const artists = inMemoryDb.artists;
  const albums = inMemoryDb.albums;
  const playlists = inMemoryDb.playlists;

  // Decorate songs with favorite status if user is authenticated
  const userFavorites = req.user
    ? new Set(inMemoryDb.favorites.filter((f) => f.user_id === req.user?.id).map((f) => f.song_id))
    : new Set<string>();

  const decoratedSongs = songs.map((s) => ({
    ...s,
    is_favorite: userFavorites.has(s.id),
  }));

  const quickPicks = decoratedSongs.slice(0, 6);
  const jumpBackIn = decoratedSongs.slice(2, 6);
  const newReleases = albums.slice(0, 4);

  const genres = [
    { id: 'gen_synthwave', name: 'Synthwave', color: '#6750A4' },
    { id: 'gen_cyberpunk', name: 'Cyberpunk', color: '#00F0FF' },
    { id: 'gen_lofi', name: 'Lo-Fi Chill', color: '#FFB74D' },
    { id: 'gen_ambient', name: 'Ambient', color: '#4CAF50' },
    { id: 'gen_electronic', name: 'Electronic', color: '#E91E63' },
  ];

  res.status(200).json({
    sonicChemistry: {
      matchedGenre: 'Synthwave / Cyberpunk',
      matchPercentage: 98,
      headline: 'Your Sonic Chemistry',
      description: 'You share 98% musical taste resonance with artists in this collection.',
    },
    quickPicks,
    jumpBackIn,
    popularArtists: artists,
    curatedPlaylists: playlists,
    newReleases,
    genres,
  });
});

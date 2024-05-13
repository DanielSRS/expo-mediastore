// Import the native module. On web, it will be resolved to ExpoMediastore.web.ts
// and on native platforms to ExpoMediastore.ts
import ExpoMediastoreModule from './ExpoMediastoreModule';

// Get the native constant value.
export const PI = ExpoMediastoreModule.PI;

export function hello(): string {
  return ExpoMediastoreModule.hello();
}

interface MusicMedia {
  id: number;
  name: string;
  duration: number;
  size: number;
  mime: string;
  title: string;
  album: string;
  artist: string;
  contentUri: string;
  albumId: string;
  albumArt: string;
  genreId?: string;
  genre?: string;
}
export function getMusicMedias(): MusicMedia {
  return ExpoMediastoreModule.getMusicMediasSync();
}

interface AlbumMedia {
  id: string;
  name: string;
  numberOfSongs: string;
  artist: string;
}
export function getAlbums(): AlbumMedia {
  return ExpoMediastoreModule.readAlbumsSync();
}

interface Genre {
  id: string;
  name: string;
}
export function getGenresSync(): Genre {
  return ExpoMediastoreModule.getGenresSync();
}

interface GenreMember {
  id: string;
  name: string;
  artistId: string;
  albumId: string;
}
export function getGenreMediasSync(genreId: string): GenreMember {
  return ExpoMediastoreModule.getGenreMediasSync(genreId);
}

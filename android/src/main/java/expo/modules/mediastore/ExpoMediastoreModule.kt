package expo.modules.mediastore

import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

class MusicMedia : Record {
  @Field var id: Long = -1
  @Field var name: String = ""
  @Field var duration: Int = -1
  @Field var size: Int = -1
  @Field var mime: String = ""
  @Field var title: String = ""
  @Field var album: String = ""
  @Field var artist: String = ""
  @Field var contentUri: String = ""
  @Field var albumId: String = ""
  @Field var albumArt: String = ""
  @Field var genreId: String? = null
  @Field var genre: String? = null
}

class AlbumMedia : Record {
  @Field var id: String = ""
  @Field var name: String = ""
  @Field var numberOfSongs: String = ""
  @Field var artist: String = ""
}

class Genre : Record {
  @Field var id: String = ""
  @Field var name: String = ""
}

class GenreMember : Record {
  @Field var id: String = ""
  @Field var name: String = ""
  @Field var artistId: String = ""
  @Field var albumId: String = ""
}

class FullMusicMedia : Record {
  @Field var id: Long = -1
  @Field var name: String = ""
  @Field var duration: Int = -1
  @Field var size: Int = -1
  @Field var mime: String = ""
  @Field var title: String = ""
  @Field var album: String = ""
  @Field var artist: String = ""
  @Field var contentUri: String = ""
  @Field var albumId: String = ""
  @Field var albumArt: String = ""
  @Field var genreId: String? = null
  @Field var genre: String? = null
  @Field var addedAt: Int = 0
  @Field var modifiedAt: Int = 0
  @Field var composer: String = ""
  @Field var artistId: String = ""
  @Field var cdTrackNumber: String = ""
  @Field var isFavorite: Int = -1
  @Field var year: Int = 0
  @Field var path: String = ""
}

class ExpoMediastoreModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoMediastore")

    Function("getMusicMediasSync") {
      getMusicMediasSync()
    }

    Function("readAlbumsSync") {
      readAlbumsSync()
    }

    Function("getGenresSync") {
      getGenresSync()
    }

    Function("getGenreMediasSync") { genreId: String ->
      getGenreMediasSync(genreId)
    }

    Function("getFullMusicMediaByIdSync") { musicId: String ->
      getFullMusicMediaByIdSync(musicId)
    }
  }

  private fun getMusicMediasSync(): Array<MusicMedia> {

    val files = mutableListOf<MusicMedia>()
    val collectionProp = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
      MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    })

    val externalContentUriProp = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val idColumnProp = MediaStore.Audio.Media._ID
    val nameColumnProp = MediaStore.Audio.Media.DISPLAY_NAME
    val durationColumnProp = MediaStore.Audio.Media.DURATION
    val sizeColumnProp = MediaStore.Audio.Media.SIZE
    val mimeColumnProp = MediaStore.Audio.Media.MIME_TYPE
    val titleColumnProp = MediaStore.Audio.Media.TITLE
    val albumColumnProp = MediaStore.Audio.Media.ALBUM
    val artistColumnProp = MediaStore.Audio.Media.ARTIST
    val albumIdProp = MediaStore.Audio.Media.ALBUM_ID

    val canRetrieveGenre = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    var genreIdProp = ""
    var genreProp = ""

    val projection = mutableListOf(
            idColumnProp,
            nameColumnProp,
            durationColumnProp,
            sizeColumnProp,
            mimeColumnProp,
            titleColumnProp,
            albumColumnProp,
            artistColumnProp,
            albumIdProp,
    )
    if (canRetrieveGenre) {
      genreIdProp = MediaStore.Audio.Media.GENRE_ID
      genreProp = MediaStore.Audio.Media.GENRE
      projection.add(genreIdProp)
      projection.add(genreProp)
    }

    val query = appContext.reactContext?.contentResolver!!.query(
            collectionProp,
            projection.toTypedArray(),
            null,
            null,
            null
    )
    query?.use { cursor ->
      val idColumn = cursor.getColumnIndexOrThrow(idColumnProp)
      val nameColumn = cursor.getColumnIndexOrThrow(nameColumnProp)
      val durationColumn = cursor.getColumnIndexOrThrow(durationColumnProp)
      val sizeColumn = cursor.getColumnIndexOrThrow(sizeColumnProp)
      val mimeColumn = cursor.getColumnIndexOrThrow(mimeColumnProp)
      val titleColumn = cursor.getColumnIndexOrThrow(titleColumnProp)
      val albumColumn = cursor.getColumnIndexOrThrow(albumColumnProp)
      val artistColumn = cursor.getColumnIndexOrThrow(artistColumnProp)
      val albumIdColumn = cursor.getColumnIndexOrThrow(albumIdProp)
      var genreIdColumn = 0
      var genreColumn = 0
      if (canRetrieveGenre) {
        genreIdColumn = cursor.getColumnIndexOrThrow(genreIdProp)
        genreColumn = cursor.getColumnIndexOrThrow(genreProp)
      }

      while (cursor.moveToNext()) {
        val audioId = cursor.getLong(idColumn)
        val albumID = cursor.getLong(albumIdColumn).toString()
        val item = MusicMedia().apply {
          id = audioId
          name = cursor.getString(nameColumn)
          duration = cursor.getInt(durationColumn)
          size = cursor.getInt(sizeColumn)
          mime = cursor.getString(mimeColumn)
          title = cursor.getString(titleColumn)
          album = cursor.getString(albumColumn)
          artist = cursor.getString(artistColumn)
          contentUri = "content://media" + externalContentUriProp.path + "/" + audioId
          albumId = albumID
          albumArt = "content://media/external/audio/albumart/$albumID"
          if (canRetrieveGenre) {
            genreId = cursor.getString(genreIdColumn)
            genre = cursor.getString(genreColumn)
          }
        }

        files.add(item)
      }
    }

    return files.toTypedArray()
  }

  private fun readAlbumsSync(): Array<AlbumMedia> {
    val files = mutableListOf<AlbumMedia>()

    val collectionProp = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      MediaStore.Audio.Albums.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
      MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
    })

    val idColumnProp = MediaStore.Audio.Albums._ID
    val nameColumnProp = MediaStore.Audio.Albums.ALBUM
    val numberOfSongsColumnProp = MediaStore.Audio.Albums.NUMBER_OF_SONGS
    val artistColumnProp = MediaStore.Audio.Albums.ARTIST

    val projection = arrayOf(
            idColumnProp,
            nameColumnProp,
            numberOfSongsColumnProp,
            artistColumnProp,
    )
    val query = appContext.reactContext?.contentResolver!!.query(
            collectionProp,
            projection,
            null,
            null,
            null
    )
    query?.use { cursor ->
      val idColumn = cursor.getColumnIndexOrThrow(idColumnProp)
      val nameColumn = cursor.getColumnIndexOrThrow(nameColumnProp)
      val numberOfSongsColumn = cursor.getColumnIndexOrThrow(numberOfSongsColumnProp)
      val artistColumn = cursor.getColumnIndexOrThrow(artistColumnProp)

      while (cursor.moveToNext()) {
        val albumID = cursor.getLong(idColumn).toString()
        val item = AlbumMedia().apply {
          id = albumID
          name = cursor.getString(nameColumn)
          numberOfSongs = cursor.getString(numberOfSongsColumn)
          artist = cursor.getString(artistColumn)
        }

        files.add(item)
      }
    }

    return files.toTypedArray()
  }

  private fun getGenresSync(): Array<Genre> {
    val collectionProp = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      MediaStore.Audio.Genres.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
      MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
    })
    val idColumnProp = MediaStore.Audio.Genres._ID
    val nameColumnProp = MediaStore.Audio.Genres.NAME

    val projection = arrayOf(
            idColumnProp,
            nameColumnProp,
    )
    val query = appContext.reactContext?.contentResolver!!.query(
            collectionProp,
            projection,
            null,
            null,
            null
    )

    val files = mutableListOf<Genre>()

    query?.use { cursor ->
      val idColumn = cursor.getColumnIndexOrThrow(idColumnProp)
      val nameColumn = cursor.getColumnIndexOrThrow(nameColumnProp)

      while (cursor.moveToNext()) {
        val genreID = cursor.getLong(idColumn).toString()
        val item = Genre().apply {
          id = genreID
          name = cursor.getString(nameColumn)
        }

        files.add(item)
      }
    }

    return files.toTypedArray()
  }

  private fun getGenreMediasSync(targetGenreId: String): Array<GenreMember> {
    val files = mutableListOf<GenreMember>()
    val genreId = targetGenreId.toLong()

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      return files.toTypedArray()
    }

    val collectionProp = MediaStore.Audio.Genres.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, genreId)
    // val externalContentUriProp = ""
    val audioIdProp = MediaStore.Audio.Genres.Members._ID
    val audioNameProp = MediaStore.Audio.Genres.Members.TITLE
    val artistProp = MediaStore.Audio.Genres.Members.ARTIST
    val albumIdProp = MediaStore.Audio.Genres.Members.ALBUM_ID

    val projection = arrayOf(
            audioIdProp,
            audioNameProp,
            artistProp,
            albumIdProp
    )
    val query = appContext.reactContext?.contentResolver!!.query(
            collectionProp,
            projection,
            null,
            null,
            null
    )

    query?.use { cursor ->
      val idColumn = cursor.getColumnIndexOrThrow(audioIdProp)
      val nameColumn = cursor.getColumnIndexOrThrow(audioNameProp)
      val artistColumn = cursor.getColumnIndexOrThrow(artistProp)
      val albumIdColumn = cursor.getColumnIndexOrThrow(albumIdProp)

      while (cursor.moveToNext()) {
        val genreMemberID = cursor.getLong(idColumn).toString()
        val item = GenreMember().apply {
          id = genreMemberID
          name = cursor.getString(nameColumn)
          artistId = cursor.getString(artistColumn)
          albumId = cursor.getString(albumIdColumn)
        }

        files.add(item)
      }
    }

    return files.toTypedArray()
  }

  private fun getFullMusicMediaByIdSync(targetSongId: String): FullMusicMedia? {
    val songId = targetSongId.toLong()
    val collectionProp = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
    val idColumnProp = MediaStore.Audio.Media._ID
    val nameColumnProp = MediaStore.Audio.Media.DISPLAY_NAME
    val durationColumnProp = MediaStore.Audio.Media.DURATION
    val sizeColumnProp = MediaStore.Audio.Media.SIZE
    val mimeColumnProp = MediaStore.Audio.Media.MIME_TYPE
    val titleColumnProp = MediaStore.Audio.Media.TITLE
    val albumColumnProp = MediaStore.Audio.Media.ALBUM
    val artistColumnProp = MediaStore.Audio.Media.ARTIST
    val albumIdProp = MediaStore.Audio.Media.ALBUM_ID
    val addedAtProp = MediaStore.Audio.Media.DATE_ADDED
    val modifiedAtProp  = MediaStore.Audio.Media.DATE_MODIFIED
    val composerProp = MediaStore.Audio.Media.COMPOSER
    val artistIdProp = MediaStore.Audio.Media.ARTIST_ID
    val cdTrackNumberProp = MediaStore.Audio.Media.CD_TRACK_NUMBER
    val isFavoriteProp = MediaStore.Audio.Media.IS_FAVORITE
    val yearProp = MediaStore.Audio.Media.YEAR
    val pathProp = MediaStore.Audio.Media.DATA


    val canRetrieveGenre = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    var genreIdProp = ""
    var genreProp = ""

    val projection = mutableListOf(
            idColumnProp,
            nameColumnProp,
            durationColumnProp,
            sizeColumnProp,
            mimeColumnProp,
            titleColumnProp,
            albumColumnProp,
            artistColumnProp,
            albumIdProp,
            addedAtProp ,
            modifiedAtProp ,
            composerProp,
            artistIdProp,
            cdTrackNumberProp,
            isFavoriteProp,
            yearProp,
            pathProp,
    )
    if (canRetrieveGenre) {
      genreIdProp = MediaStore.Audio.Media.GENRE_ID
      genreProp = MediaStore.Audio.Media.GENRE
      projection.add(genreIdProp)
      projection.add(genreProp)
    }

    val cursor = appContext.reactContext?.contentResolver!!.query(
            collectionProp,
            projection.toTypedArray(),
            null,
            null,
            null
    )

    val fullMusicMedia = FullMusicMedia()

    if (cursor != null && cursor.moveToFirst()) {
      val idColumn = cursor.getColumnIndexOrThrow(idColumnProp)
      val nameColumn = cursor.getColumnIndexOrThrow(nameColumnProp)
      val durationColumn = cursor.getColumnIndexOrThrow(durationColumnProp)
      val sizeColumn = cursor.getColumnIndexOrThrow(sizeColumnProp)
      val mimeColumn = cursor.getColumnIndexOrThrow(mimeColumnProp)
      val titleColumn = cursor.getColumnIndexOrThrow(titleColumnProp)
      val albumColumn = cursor.getColumnIndexOrThrow(albumColumnProp)
      val artistColumn = cursor.getColumnIndexOrThrow(artistColumnProp)
      val albumIdColumn = cursor.getColumnIndexOrThrow(albumIdProp)
      val addedAtColumn = cursor.getColumnIndexOrThrow(addedAtProp)
      val modifiedAtColumn = cursor.getColumnIndexOrThrow(modifiedAtProp)
      val composerColumn = cursor.getColumnIndexOrThrow(composerProp)
      val artistIdColumn = cursor.getColumnIndexOrThrow(artistIdProp)
      val cdTrackNumberColumn = cursor.getColumnIndexOrThrow(cdTrackNumberProp)
      val isFavoriteColumn = cursor.getColumnIndexOrThrow(isFavoriteProp)
      val yearColumn = cursor.getColumnIndexOrThrow(yearProp)
      val pathColumn = cursor.getColumnIndexOrThrow(pathProp)

      var genreIdColumn = 0
      var genreColumn = 0
      if (canRetrieveGenre) {
        genreIdColumn = cursor.getColumnIndexOrThrow(genreIdProp)
        genreColumn = cursor.getColumnIndexOrThrow(genreProp)
      }

      val audioId = cursor.getLong(idColumn)
      val albumID = cursor.getLong(albumIdColumn).toString()
      fullMusicMedia.apply {
        id = audioId
        name = cursor.getString(nameColumn)
        duration = cursor.getInt(durationColumn)
        size = cursor.getInt(sizeColumn)
        mime = cursor.getString(mimeColumn)
        title = cursor.getString(titleColumn)
        album = cursor.getString(albumColumn)
        artist = cursor.getString(artistColumn)
        contentUri = "content://media" + collectionProp.path
        albumId = albumID
        albumArt = "content://media/external/audio/albumart/$albumID"
        if (canRetrieveGenre) {
          genreId = cursor.getString(genreIdColumn)
          genre = cursor.getString(genreColumn)
        }
        addedAt = cursor.getInt(addedAtColumn)
        modifiedAt = cursor.getInt(modifiedAtColumn)
        composer = cursor.getString(composerColumn)
        artistId = cursor.getLong(artistIdColumn).toString()
        cdTrackNumber = cursor.getString(cdTrackNumberColumn)
        isFavorite = cursor.getInt(isFavoriteColumn)
        year = cursor.getInt(yearColumn)
        path = cursor.getString(pathColumn)
      }

      cursor.close()
      return fullMusicMedia
    }

    cursor?.close()

    return null
  }
}

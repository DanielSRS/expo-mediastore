package expo.modules.mediastore

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

import android.os.Build
import android.provider.MediaStore

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
}

class ExpoMediastoreModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoMediastore")

    Function("getMusicMediasSync") {
      getMusicMediasSync()
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

    val projection = arrayOf(
            idColumnProp,
            nameColumnProp,
            durationColumnProp,
            sizeColumnProp,
            mimeColumnProp,
            titleColumnProp,
            albumColumnProp,
            artistColumnProp,
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
      val idColumn = cursor.getColumnIndexOrThrow(idColumnProp)
      val nameColumn = cursor.getColumnIndexOrThrow(nameColumnProp)
      val durationColumn = cursor.getColumnIndexOrThrow(durationColumnProp)
      val sizeColumn = cursor.getColumnIndexOrThrow(sizeColumnProp)
      val mimeColumn = cursor.getColumnIndexOrThrow(mimeColumnProp)
      val titleColumn = cursor.getColumnIndexOrThrow(titleColumnProp)
      val albumColumn = cursor.getColumnIndexOrThrow(albumColumnProp)
      val artistColumn = cursor.getColumnIndexOrThrow(artistColumnProp)
      val albumIdColumn = cursor.getColumnIndexOrThrow(albumIdProp)

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
        }

        files.add(item)
      }
    }

    return files.toTypedArray()
  }
}

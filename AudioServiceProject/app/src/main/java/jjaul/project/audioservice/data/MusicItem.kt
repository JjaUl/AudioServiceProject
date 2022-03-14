package jjaul.project.audioservice.data

import com.google.android.exoplayer2.MediaItem

data class MusicItem(val id: Int,
                     val title: String?,
                     val artist: String?,
                     val coverImg: ByteArray?,
                     val mediaItem: MediaItem,
                     var isPlaying: Boolean = false) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MusicItem

        if (title != other.title) return false
        if (artist != other.artist) return false
        if (coverImg != null) {
            if (other.coverImg == null) return false
            if (!coverImg.contentEquals(other.coverImg)) return false
        } else if (other.coverImg != null) return false
        if (mediaItem != other.mediaItem) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (artist?.hashCode() ?: 0)
        result = 31 * result + (coverImg?.contentHashCode() ?: 0)
        result = 31 * result + mediaItem.hashCode()
        return result
    }
}

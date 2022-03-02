package jjaul.project.audioservice.data

data class MusicItem(/*val id: String,*/
                     val title: String?,
                     val artist: String?,
                     val coverImg: ByteArray?,
                     var isSelected: Boolean = false) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MusicItem

        if (title != other.title) return false
        if (artist != other.artist) return false
        if (!coverImg.contentEquals(other.coverImg)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + coverImg.contentHashCode()
        return result
    }
}

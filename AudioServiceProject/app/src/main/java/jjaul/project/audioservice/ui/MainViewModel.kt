package jjaul.project.audioservice.ui

import android.app.Application
import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.lifecycle.AndroidViewModel
import com.google.android.exoplayer2.MediaItem
import jjaul.project.audioservice.data.MusicItem

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val context: Context = application

    var controllerList: MutableList<MediaItem>? = null
    var playLists: MutableList<MusicItem>? = null

    private fun getAssetsItems(): MutableList<MusicItem> {
        val myAssets = context.assets
        val assetsItem = myAssets?.list("music")
        val list = mutableListOf<MusicItem>()

        if (assetsItem != null) {
            for (element in assetsItem) {
                val fd = myAssets.openFd("music/${element}")
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)

                list.add(
                    MusicItem(title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                        artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                        coverImg = mmr.embeddedPicture)
                )
            }
        }

        return list
    }
}
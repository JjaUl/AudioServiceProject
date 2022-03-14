package jjaul.project.audioservice.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ManageMusicItem(private val context: Context) {

    companion object {
        // Volatile : 메인 메모리에 변수 저장
        @Volatile private var instance: ManageMusicItem? = null

        @JvmStatic fun getInstance(context: Context): ManageMusicItem = instance?: synchronized(this) {
            ManageMusicItem(context).also { instance = it }
        }
    }

    private var itemList: MutableList<MusicItem>
    var playIdx: Int = -1

    fun setList(list: MutableList<MusicItem>) {
        this.itemList = list
    }

    fun getCurrentItem() = itemList[playIdx]

    fun getIdxItem(idx: Int) = itemList[idx]

    fun getList() = itemList

    fun playItem(idx: Int) {
        if (playIdx != -1)
            itemList[playIdx].isPlaying = false
        playIdx = idx
        itemList[playIdx].isPlaying = true
    }

    init {
        setSubject()
        itemList = setAssetsItems()
    }

    private fun setAssetsItems(): MutableList<MusicItem> {
        val myAssets = context.assets
        val assetsItem = myAssets?.list("music")
        val list = mutableListOf<MusicItem>()

        if (assetsItem != null) {
            for (element in assetsItem) {
                val fd = myAssets.openFd("music/${element}")
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)

                val item = MusicItem(id = list.size,
                    title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                    artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                    coverImg = mmr.embeddedPicture,
                    mediaItem = MediaItem.fromUri(Uri.parse("asset:///music/${element}")))

                list.add(item)
            }
            listSubject?.onNext(list)
        }

        return list
    }

    private var listSubject: BehaviorSubject<MutableList<MusicItem>>? = null
    private var itemSubject: BehaviorSubject<MusicItem>? = null

    private fun setSubject() {
        listSubject = BehaviorSubject.create()
        itemSubject = BehaviorSubject.create()
    }

    fun getMusicListSubject(): BehaviorSubject<MutableList<MusicItem>>? {
        return listSubject
    }

    fun getMusicItemSubject(): BehaviorSubject<MusicItem>? {
        return itemSubject
    }
}
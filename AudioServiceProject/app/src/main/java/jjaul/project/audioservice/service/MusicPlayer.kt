package jjaul.project.audioservice.service

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class MusicPlayer(context: Context) {
    private var player: ExoPlayer? = null
    private var isPlayerStarting: Boolean = false
    private var isPlayerPlaying: Boolean = false

    init {
        player = ExoPlayer.Builder(context).build()
    }

    public fun getPlayer() = player

    public fun addListener(listener: Player.Listener) {
        player?.addListener(listener)
    }

    public fun isPlaying(): Boolean {
        return isPlayerStarting && isPlayerPlaying
    }

    public fun isPause(): Boolean {
        return when {
            isPlayerStarting && !isPlayerPlaying -> true
            !isPlayerStarting -> true
            else -> false
        }
    }

    public fun isStart(): Boolean = isPlayerStarting

    public fun setList(list: MutableList<MediaItem>) {
        player?.setMediaItems(list)
    }

    public fun setItem(item: MediaItem) {
        player?.setMediaItem(item)
    }

    public fun addItem(item: MediaItem) {
        player?.addMediaItem(item)
    }

    public fun currentIdx(): Int = player!!.currentMediaItemIndex

    public fun playIdx(idx: Int) {
        player?.seekTo(idx, 0)
    }

    public fun play() {
        player?.let {
            it.prepare()
            it.play()
            isPlayerStarting = true
            isPlayerPlaying = true
        }
    }

    public fun pause() {
        player?.pause()
        isPlayerPlaying = false
    }

    public fun control() {
        player?.let {
            if (isPlaying()) {
                pause()
            } else
                play()
        }
    }

    public fun next(): Int {
        player?.let {
            if (it.hasNextMediaItem()) {
                it.seekToNextMediaItem()
                it.playWhenReady = true
                return it.currentMediaItemIndex
            }
        }

        return -1
    }

    public fun prev(): Int {
        player?.let {
            if (it.hasPreviousMediaItem()) {
                it.seekToPreviousMediaItem()
                it.playWhenReady = true
                return it.currentMediaItemIndex
            }
        }

        return -1
    }

    public fun stop() {
        player?.let {
            it.pause()
            it.stop()
            it.release()
            isPlayerStarting = false
            isPlayerPlaying = false
        }
    }
}
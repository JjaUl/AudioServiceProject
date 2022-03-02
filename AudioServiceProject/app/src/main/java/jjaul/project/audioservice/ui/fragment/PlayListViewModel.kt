package jjaul.project.audioservice.ui.fragment

import androidx.databinding.BaseObservable
import jjaul.project.audioservice.data.MusicItem

class PlayListViewModel: BaseObservable() {
    private var musicList = mutableListOf<MusicItem>()

    fun load() {
        notifyChange()
    }
}
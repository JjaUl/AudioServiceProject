package jjaul.project.audioservice.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import jjaul.project.audioservice.ui.MainActivity
import jjaul.project.audioservice.R
import jjaul.project.audioservice.databinding.FragmentControllerBinding
import jjaul.project.audioservice.service.MusicPlayer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MediaControllerFragment: BaseFragment<FragmentControllerBinding>(FragmentControllerBinding::inflate) {
    private var activity: MainActivity? = null
    private var player: MusicPlayer? = null
    private var seekbarDisposable: Disposable? = null
    private var itemDisposable: Disposable? = null
    private var listDisposable: Disposable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserving()
        bindingViewEvent()
    }

    private fun setObserving() {
        itemDisposable = activity?.manager?.getMusicItemSubject()?.subscribe { it ->
            player?.playIdx(it.id)
            player?.play()
        }

        listDisposable = activity?.manager?.getMusicListSubject()?.subscribe({ list ->
            player = context?.let {
                MusicPlayer(it).apply {
                    setList(list)
                    addListener(playerListener)
                }
            }
        }, { Toast.makeText(context,"음악리스트 로딩 실패!", Toast.LENGTH_SHORT).show() })
    }

    private fun bindingViewEvent() {
        binding.btnPlayControl.setOnClickListener {
            player?.let {
                it.control()
                activity?.manager?.getMusicItemSubject()?.onNext(activity?.manager?.getCurrentItem()!!)
            }
        }

        binding.btnNext.setOnClickListener {
            val nextIdx = player?.next()
            if (nextIdx != -1) {
                activity?.manager?.getMusicItemSubject()?.onNext(activity?.manager?.getIdxItem(nextIdx!!)!!)
                player?.play()
            }
        }

        binding.btnPrev.setOnClickListener {
            val prevIdx = player?.prev()
            if (prevIdx != -1) {
                activity?.manager?.getMusicItemSubject()?.onNext(activity?.manager?.getIdxItem(prevIdx!!)!!)
                player?.play()
            }
        }

        binding.playerSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                setTime(binding.currentPalyTime, (p1 * 1000).toLong())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.progress?.times(1000)?.let { player?.getPlayer()?.seekTo(it.toLong()) }
            }

        })
    }

    override fun onDestroyView() {
        player?.stop()
        disposeObserve()
        super.onDestroyView()
    }

    private fun disposeObserve() {
        seekbarDisposable?.dispose()
        itemDisposable?.dispose()
        listDisposable?.dispose()
    }

    private val playerListener = object: Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                binding.btnPlayControl.setImageResource(R.drawable.ic_baseline_pause_36)
                seekbarDisposable = Observable.interval(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ updateSeek() }, { thr -> thr.printStackTrace()})
            } else {
                binding.btnPlayControl.setImageResource(R.drawable.ic_baseline_play_arrow_36)
                seekbarDisposable?.dispose()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            setSeekDate(playbackState)
        }

        // seekbar 초기 세팅
        private fun setSeekDate(state: Int) {
            when(state) {
                ExoPlayer.STATE_READY -> {
                    player?.let {
                        binding.playerSeekbar.max = (it.getPlayer()?.duration!!/1000).toInt()
                        binding.playerSeekbar.progress = 0
                        setTime(binding.duration, it.getPlayer()?.duration!!)
                    }
                }
                ExoPlayer.STATE_ENDED -> {
                    player?.let {
                        binding.playerSeekbar.max = (it.getPlayer()?.duration!!/1000).toInt()
                        binding.playerSeekbar.progress = binding.playerSeekbar.max
                    }
                }
            }
        }
    }

    // duration, progress 시간 설정
    private fun setTime(view: TextView, time: Long) {
        val timeDate = Date(time)
        val timeFormat = SimpleDateFormat("mm:ss")
        timeFormat.format(timeDate)
        view.text = timeFormat.format(timeDate)
    }

    // seekbar update
    private fun updateSeek() {
        player?.let {
            binding.playerSeekbar.progress =
                (it.getPlayer()?.currentPosition!! / 1000).toInt()
        }
    }
}
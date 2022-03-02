package jjaul.project.audioservice.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import jjaul.project.audioservice.ui.MainActivity
import jjaul.project.audioservice.R
import jjaul.project.audioservice.databinding.FragmentControllerBinding
import jjaul.project.audioservice.service.MusicPlayer
import java.text.SimpleDateFormat
import java.util.*

class MediaControllerFragment: BaseFragment<FragmentControllerBinding>(FragmentControllerBinding::inflate) {
    private var activity: MainActivity? = null
    private var player: MusicPlayer? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("getListPos") { _, bundle ->
            player?.playIdx(bundle.getInt("pos"))
            player?.play()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player = context?.let {
            MusicPlayer(it).apply {
                setList(getAssetsItems())
                addListener(playerListener)
            }
        }

        binding.btnPlayControl.setOnClickListener {
            player?.let {
                it.control()
                setFragmentResult("getPlayerPos", bundleOf("pos" to it.currentIdx()))
                setFragmentResult("getCurrentItem", bundleOf("pos" to it.currentIdx()))
            }
        }

        binding.btnNext.setOnClickListener {
            val nextIdx = player?.next()
            if (nextIdx != -1) {
                setFragmentResult("getPlayerPos", bundleOf("pos" to nextIdx))
                setFragmentResult("getCurrentItem", bundleOf("pos" to nextIdx))
                player?.play()
            }
        }

        binding.btnPrev.setOnClickListener {
            val prevIdx = player?.prev()
            if (prevIdx != -1) {
                setFragmentResult("getPlayerPos", bundleOf("pos" to prevIdx))
                setFragmentResult("getCurrentItem", bundleOf("pos" to prevIdx))
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
        binding.playerSeekbar.removeCallbacks(updateSeekbarRunnable)
        super.onDestroyView()
    }

    private val playerListener = object: Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                binding.btnPlayControl.setImageResource(R.drawable.ic_baseline_pause_36)
                binding.playerSeekbar.post(updateSeekbarRunnable)
            } else {
                binding.btnPlayControl.setImageResource(R.drawable.ic_baseline_play_arrow_36)
                binding.playerSeekbar.removeCallbacks(updateSeekbarRunnable)
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            setSeekDate(playbackState)
        }

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

    private fun setTime(view: TextView, time: Long) {
        val timeDate = Date(time)
        val timeFormat = SimpleDateFormat("mm:ss")
        timeFormat.format(timeDate)
        view.text = timeFormat.format(timeDate)
    }

    private val updateSeekbarRunnable = Runnable {
        updateSeek()
    }

    private fun updateSeek() {
        player?.let {
            binding.playerSeekbar.progress =
                (it.getPlayer()?.currentPosition!! / 1000).toInt()
        }

        binding.playerSeekbar.postDelayed(updateSeekbarRunnable, 500)
    }

    private fun getAssetsItems(): MutableList<MediaItem> {
        val asset = activity?.assets
        val assetsItem = asset?.list("music")
        val list = mutableListOf<MediaItem>()

        if (assetsItem != null) {
            for (element in assetsItem) {
                val item = MediaItem.fromUri(Uri.parse("asset:///music/${element}"))
                list.add(item)
            }
        }

        return list
    }
}
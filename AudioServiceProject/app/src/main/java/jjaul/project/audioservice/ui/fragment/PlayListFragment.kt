package jjaul.project.audioservice.ui.fragment

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import jjaul.project.audioservice.ui.MainActivity
import jjaul.project.audioservice.data.MusicItem
import jjaul.project.audioservice.databinding.FragmentPlayListBinding
import jjaul.project.audioservice.ui.adapter.MusicListAdapter

class PlayListFragment: BaseFragment<FragmentPlayListBinding>(FragmentPlayListBinding::inflate) {
    private var activity: MainActivity? = null
    private lateinit var recyclerAdapter: MusicListAdapter

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

        setFragmentResultListener("getPlayerPos") { _, bundle ->
            val eventPos = bundle.getInt("pos")
            recyclerAdapter.setSelectedItem(eventPos)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
    }

    private fun initRecycler() {
        recyclerAdapter = MusicListAdapter()
        (binding.musicRecyclerview.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.musicRecyclerview.adapter = recyclerAdapter

        recyclerAdapter.apply {
            setMusicItem(getAssetsItems())
            setOnItemClickListener(object : MusicListAdapter.OnItemClickListener {
                override fun onItemClick(v: View, pos: Int) {
                    setFragmentResult("getListPos", bundleOf("pos" to pos))
                    setFragmentResult("getCurrentItem", bundleOf("pos" to pos))
                    activity?.attachDetailFragment()
                    (this@apply as MusicListAdapter).let {
                        it.setSelectedItem(pos)
                    }
                }
            })
            notifyDataSetChanged()
        }
    }

    private fun getAssetsItems(): MutableList<MusicItem> {
        val myAssets = activity?.assets
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
                        coverImg = mmr.embeddedPicture))
            }
        }

        return list
    }
}
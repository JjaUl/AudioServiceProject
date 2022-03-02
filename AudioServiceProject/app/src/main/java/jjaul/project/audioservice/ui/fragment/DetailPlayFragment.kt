package jjaul.project.audioservice.ui.fragment

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import jjaul.project.audioservice.R
import jjaul.project.audioservice.data.MusicItem
import jjaul.project.audioservice.ui.MainActivity
import jjaul.project.audioservice.databinding.FragmentDetailPlayBinding

class DetailPlayFragment: BaseFragment<FragmentDetailPlayBinding>(FragmentDetailPlayBinding::inflate) {
    private var activity: MainActivity? = null
    private var list: MutableList<MusicItem>? = null


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

        setFragmentResultListener("getCurrentItem") { _, bundle ->
            val eventPos = bundle.getInt("pos")
            setItem(eventPos)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.let {
                it.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_from_left,R.anim.slide_to_right).remove(this@DetailPlayFragment).commit()
            }
        }
    }

    private fun setItem(pos: Int) {
        binding.detailTitle.text = list?.get(pos)?.title
        binding.detailArtist.text = list?.get(pos)?.artist
        Glide.with(binding.detailCover).load(list?.get(pos)?.coverImg).let {
            if(binding.detailCover.drawable != null) {
                it.placeholder(binding.detailCover.drawable.constantState?.newDrawable()?.mutate())
            } else {
                it
            }
        }.into(binding.detailCover)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list = getAssetsItems()
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
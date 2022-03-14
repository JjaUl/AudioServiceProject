package jjaul.project.audioservice.ui.fragment

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.disposables.Disposable
import jjaul.project.audioservice.R
import jjaul.project.audioservice.data.MusicItem
import jjaul.project.audioservice.ui.MainActivity
import jjaul.project.audioservice.databinding.FragmentDetailPlayBinding

class DetailPlayFragment: BaseFragment<FragmentDetailPlayBinding>(FragmentDetailPlayBinding::inflate) {
    private var activity: MainActivity? = null
    private var disposable: Disposable? = null
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

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right)
                    .remove(this@DetailPlayFragment)
                    .commit()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposable = activity?.manager?.getMusicItemSubject()?.subscribe { it -> setItem(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

    private fun setItem(item: MusicItem) {
        binding.detailTitle.text = item.title
        binding.detailArtist.text = item.artist
        Glide.with(binding.detailCover).load(item.coverImg).let {
            if(binding.detailCover.drawable != null) {
                it.placeholder(binding.detailCover.drawable.constantState?.newDrawable()?.mutate())
            } else {
                it
            }
        }.into(binding.detailCover)
    }
}
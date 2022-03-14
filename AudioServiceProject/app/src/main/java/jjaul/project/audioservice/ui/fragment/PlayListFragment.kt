package jjaul.project.audioservice.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.recyclerview.widget.SimpleItemAnimator
import io.reactivex.rxjava3.disposables.Disposable
import jjaul.project.audioservice.ui.MainActivity
import jjaul.project.audioservice.data.MusicItem
import jjaul.project.audioservice.databinding.FragmentPlayListBinding
import jjaul.project.audioservice.ui.adapter.MusicListAdapter

class PlayListFragment: BaseFragment<FragmentPlayListBinding>(FragmentPlayListBinding::inflate) {
    private var activity: MainActivity? = null
    private lateinit var recyclerAdapter: MusicListAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listDisposable = activity?.manager?.getMusicListSubject()?.subscribe({ initRecycler(it) },
            { Toast.makeText(context,"음악리스트 로딩 실패!", Toast.LENGTH_SHORT).show()})

        itemDisposable = activity?.manager?.getMusicItemSubject()?.subscribe { it ->
            activity?.manager?.playItem(it.id)
            recyclerAdapter.notifyDataSetChanged()
        }
    }


    override fun onDestroyView() {
        disposeObserve()
        super.onDestroyView()
    }

    private fun disposeObserve() {
        itemDisposable?.dispose()
        listDisposable?.dispose()
    }

    private fun initRecycler(list: MutableList<MusicItem>) {
        recyclerAdapter = MusicListAdapter(context!!)
        (binding.musicRecyclerview.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.musicRecyclerview.adapter = recyclerAdapter

        recyclerAdapter.apply {
            setOnItemClickListener(object : MusicListAdapter.OnItemClickListener {
                override fun onItemClick(v: View, pos: Int) {
                    activity?.manager?.getMusicItemSubject()?.onNext(list[pos])
                    activity?.attachDetailFragment()
                }
            })
            notifyDataSetChanged()
        }
    }
}
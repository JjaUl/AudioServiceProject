package jjaul.project.audioservice.ui.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaMetadata
import jjaul.project.audioservice.R
import jjaul.project.audioservice.data.MusicItem

class MusicListAdapter: RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {
    private var list = mutableListOf<MusicItem>()
    private var itemListener: OnItemClickListener? = null
    private var curIdx: Int = 0

    public interface OnItemClickListener {
        fun onItemClick(v: View, pos: Int)
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_musiclist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])

        if (list[position].isSelected) {
            holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            holder.title.isSelected = true
            holder.subtitle.isSelected = true
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.title.isSelected = false
            holder.subtitle.isSelected = false
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setSelectedItem(eventIdx: Int) {
        list[curIdx].isSelected = false
        list[eventIdx].isSelected = true
        notifyItemChanged(curIdx)
        notifyItemChanged(eventIdx)
        curIdx = eventIdx
    }

    fun setMusicItem(data: MutableList<MusicItem>) {
        list = data
    }

    fun addMetaItem(metadata: MusicItem) {
        list.add(metadata)
    }

    fun isContain(metadata: MusicItem): Boolean {
        return list.contains(metadata)
    }

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        public val title: TextView = itemView.findViewById(R.id.item_title_txt)
        public val subtitle: TextView = itemView.findViewById(R.id.item_subtitle_txt)
        private val itemimg: ImageView = itemView.findViewById(R.id.item_iv)

        fun bind(item: MusicItem) {
            title.text = item.title
            subtitle.apply {
                if (TextUtils.isEmpty(item.artist))
                    visibility = View.GONE
                else
                    text = item.artist
            }

            Glide.with(itemView).load(item.coverImg).let {
                if(itemimg.drawable != null) {
                    it.placeholder(itemimg.drawable.constantState?.newDrawable()?.mutate())
                } else {
                    it
                }
            }.into(itemimg)

            view.setOnClickListener {
                itemListener?.onItemClick(view, bindingAdapterPosition)
            }
        }
    }
}
package id.xxx.example.presentation.ui.content

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.xxx.example.R
import id.xxx.example.data.source.local.M3uEntity
import id.xxx.example.databinding.ItemContentBinding

class ContentFragmentRecyclerViewAdapter(
    private val onItemClicked: (data: M3uEntity) -> Unit = { }
) : RecyclerView.Adapter<ContentFragmentRecyclerViewAdapterViewHolder>() {

    private val _items = mutableListOf<M3uEntity>()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ContentFragmentRecyclerViewAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_content, parent, false)
        return ContentFragmentRecyclerViewAdapterViewHolder(view)
    }

    override fun getItemCount() = _items.size

    override fun onBindViewHolder(
        holder: ContentFragmentRecyclerViewAdapterViewHolder,
        position: Int
    ) {
        val viewBinding = ItemContentBinding.bind(holder.itemView)
        val data = _items[position]
        Glide.with(viewBinding.root)
            .load(data.tvgLogo)
            .error(android.R.drawable.stat_notify_error)
            .into(viewBinding.imageViewLogo)
        viewBinding.textViewTvgName.text = data.channelName
        viewBinding.rootConstraintLayout.setOnClickListener {
            onItemClicked.invoke(data)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItem(items: Collection<M3uEntity>) {
        _items.clear()
        _items.addAll(items)
        notifyDataSetChanged()
    }
}
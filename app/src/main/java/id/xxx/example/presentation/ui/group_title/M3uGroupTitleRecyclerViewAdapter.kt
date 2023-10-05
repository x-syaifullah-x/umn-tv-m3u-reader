package id.xxx.example.presentation.ui.group_title

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.xxx.example.R
import id.xxx.example.databinding.ItemGroupTitleBinding

class M3uGroupTitleRecyclerViewAdapter(
    private val onItemFocus: (groupTitle: String) -> Unit = { }
) : RecyclerView.Adapter<M3uGroupTitleRecyclerViewAdapterHolder>() {

    private val _items = mutableListOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): M3uGroupTitleRecyclerViewAdapterHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_group_title, parent, false)
        return M3uGroupTitleRecyclerViewAdapterHolder(view)
    }

    override fun getItemCount() = _items.size

    override fun onBindViewHolder(holder: M3uGroupTitleRecyclerViewAdapterHolder, position: Int) {
        val viewBinding = ItemGroupTitleBinding.bind(holder.itemView)
        val groupTitle = _items[position]
        viewBinding.textViewTitle.text = groupTitle
        viewBinding.textViewTitle.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                onItemFocus.invoke(groupTitle)
        }
        viewBinding.textViewTitle.setOnClickListener {
            it.requestFocus()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItem(items: Collection<String>) {
        _items.clear()
        _items.addAll(items)
        notifyDataSetChanged()
    }
}
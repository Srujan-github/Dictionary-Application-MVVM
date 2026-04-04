package labs.creative.dictornarymvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import labs.creative.dictornarymvvmapp.databinding.ItemTrendingWordBinding

class TrendingAdapter(
    private val onWordClick: (String) -> Unit,
) : ListAdapter<TrendingWord, TrendingAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(val binding: ItemTrendingWordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrendingWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvRank.text = item.rank
        holder.binding.tvWord.text = item.word
        holder.binding.tvWordDescription.text = item.description
        holder.binding.root.setOnClickListener { onWordClick(item.word) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingWord>() {
            override fun areItemsTheSame(oldItem: TrendingWord, newItem: TrendingWord) =
                oldItem.word == newItem.word

            override fun areContentsTheSame(oldItem: TrendingWord, newItem: TrendingWord) =
                oldItem == newItem
        }
    }
}

package labs.creative.dictornarymvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import labs.creative.dictornarymvvmapp.databinding.WordCardViewBinding

/**
 * A simple word-tile card adapter used on preview / placeholder surfaces.
 * Uses its own lightweight [WordTile] model to avoid depending on the
 * domain layer's [WordInfo] in a presentation-only component.
 */
data class WordTile(
    val word: String,
    val description: String,
)

class WordsTilesAdapter : ListAdapter<WordTile, WordsTilesAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(val binding: WordCardViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            WordCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvWord.text = item.word
        holder.binding.tvWordDescription.text = item.description
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WordTile>() {
            override fun areItemsTheSame(oldItem: WordTile, newItem: WordTile) =
                oldItem.word == newItem.word
            override fun areContentsTheSame(oldItem: WordTile, newItem: WordTile) =
                oldItem == newItem
        }
    }
}

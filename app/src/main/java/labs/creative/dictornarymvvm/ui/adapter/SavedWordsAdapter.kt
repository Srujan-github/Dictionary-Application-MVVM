package labs.creative.dictornarymvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvmapp.databinding.ItemSavedWordBinding

class SavedWordsAdapter(
    private val onWordClick: (String) -> Unit,
) : ListAdapter<SavedWord, SavedWordsAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(val binding: ItemSavedWordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavedWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvWord.text = item.word
        holder.binding.tvPartOfSpeech.text = item.partOfSpeech.replaceFirstChar { it.uppercase() }
        holder.binding.tvShortDefinition.text = item.shortDefinition
        holder.binding.root.setOnClickListener { onWordClick(item.word) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SavedWord>() {
            override fun areItemsTheSame(oldItem: SavedWord, newItem: SavedWord) =
                oldItem.word == newItem.word

            override fun areContentsTheSame(oldItem: SavedWord, newItem: SavedWord) =
                oldItem == newItem
        }
    }
}

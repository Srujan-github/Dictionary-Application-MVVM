package labs.creative.dictornarymvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import labs.creative.dictornarymvvm.domain.model.WordSuggestion
import labs.creative.dictornarymvvmapp.databinding.ItemWordSearchBinding

class WordSearchAdapter(
    private val onWordClick: (String) -> Unit,
) : ListAdapter<WordSuggestion, WordSearchAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(val binding: ItemWordSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWordSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = getItem(position)
        holder.binding.tvWord.text = word.word
        holder.binding.root.setOnClickListener { onWordClick(word.word) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WordSuggestion>() {
            override fun areItemsTheSame(oldItem: WordSuggestion, newItem: WordSuggestion) =
                oldItem.word == newItem.word

            override fun areContentsTheSame(oldItem: WordSuggestion, newItem: WordSuggestion) =
                oldItem == newItem
        }
    }
}

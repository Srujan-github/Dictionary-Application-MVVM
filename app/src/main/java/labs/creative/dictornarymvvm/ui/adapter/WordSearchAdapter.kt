package labs.creative.dictornarymvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import labs.creative.dictornarymvvm.domain.model.WordSuggestion
import labs.creative.dictornarymvvmapp.databinding.WordCardViewBinding

class WordSearchAdapter(private var wordsMatch: List<WordSuggestion>) :
    RecyclerView.Adapter<WordSearchAdapter.ViewHolder>() {

    class ViewHolder(val binding: WordCardViewBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(wordsMatch: List<WordSuggestion>) {
        this.wordsMatch = wordsMatch
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            WordCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = wordsMatch.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentWord = wordsMatch[position]
        holder.binding.tvWord.text = currentWord.word
    }
}

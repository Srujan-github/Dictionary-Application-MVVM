package labs.creative.dictornary_mvvm_app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import labs.creative.dictornary_mvvm_app.databinding.WordCardViewBinding
import labs.creative.dictornary_mvvm_app.domain.model.WordSuggestion


class WordSearchAdapter(private val wordsMatch: List<WordSuggestion>) :
    RecyclerView.Adapter<WordSearchAdapter.ViewHolder>() {

    class ViewHolder(val binding: WordCardViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            WordCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return wordsMatch.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentWord = wordsMatch[position]
        holder.binding.tvWord.text = currentWord.word
    }
}
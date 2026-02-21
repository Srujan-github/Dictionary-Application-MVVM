package labs.creative.dictornarymvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import labs.creative.dictornarymvvmapp.databinding.WordCardViewBinding

data class WordInfo(
    val word: String,
    val description: String,
)

class WordsTilesAdapter(private val wordsInfo: List<WordInfo>) :
    RecyclerView.Adapter<WordsTilesAdapter.ViewHolder>() {

    class ViewHolder(val binding: WordCardViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            WordCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = wordsInfo.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wordInfo = wordsInfo[position]
        holder.binding.tvWord.text = wordInfo.word
        holder.binding.tvWordDescription.text = wordInfo.description
    }
}

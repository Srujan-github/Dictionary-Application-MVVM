package labs.creative.dictornary_mvvm_app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import labs.creative.dictornary_mvvm_app.data.remote.model.WordInfoDto
import labs.creative.dictornary_mvvm_app.databinding.WordCardViewBinding

class WordsTilesAdapter(private val wordsInfo: List<WordInfoDto>) :
    RecyclerView.Adapter<WordsTilesAdapter.ViewHolder>() {
    class ViewHolder(val binding: WordCardViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            WordCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return wordsInfo.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wordInfo = wordsInfo[position]
        holder.binding.tvWord.text = wordInfo.word
        holder.binding.tvWordDescription.text =
            wordInfo.meanings[0].definitions[0].definition
    }
}
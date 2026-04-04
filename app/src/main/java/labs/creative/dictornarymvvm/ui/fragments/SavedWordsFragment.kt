package labs.creative.dictornarymvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.ui.adapter.SavedWordsAdapter
import labs.creative.dictornarymvvm.ui.viewmodel.SavedWordsViewModel
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.FragmentSavedBinding

@AndroidEntryPoint
class SavedWordsFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SavedWordsViewModel by viewModels()
    private lateinit var adapter: SavedWordsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = SavedWordsAdapter { word ->
            val action = SavedWordsFragmentDirections.actionSavedToResult(word)
            findNavController().navigate(action)
        }
        binding.rcvSavedWords.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvSavedWords.adapter = adapter

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                @Suppress("DEPRECATION")
                val position = viewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) return
                val savedWordItem = adapter.currentList[position]
                viewModel.deleteWord(savedWordItem.word)

                Snackbar.make(binding.root, "${savedWordItem.word} deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.saveWord(savedWordItem)
                    }
                    .show()
            }
        }).attachToRecyclerView(binding.rcvSavedWords)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.savedWords.collectLatest { words ->
                adapter.submitList(words)
                binding.tvWordCount.text = when (words.size) {
                    0 -> getString(R.string.zero_words_saved)
                    else -> resources.getQuantityString(R.plurals.n_words_saved, words.size, words.size)
                }
                val isEmpty = words.isEmpty()
                binding.llEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                binding.rcvSavedWords.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

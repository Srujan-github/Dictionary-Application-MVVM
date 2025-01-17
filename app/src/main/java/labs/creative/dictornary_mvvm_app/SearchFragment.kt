package labs.creative.dictornary_mvvm_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornary_mvvm_app.databinding.FragmentSearchBinding
import labs.creative.dictornary_mvvm_app.ui.adapter.WordSearchAdapter
import javax.inject.Inject

class SearchFragment : Fragment() {


    private val viewModel: SearchViewModel  by hiltNavGraphViewModels(R.id.nav_graph)
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var wordSearchAdapter: WordSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        wordSearchAdapter = WordSearchAdapter(emptyList()) // Initialize with an empty list

        binding.rcvWordSuggestions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvWordSuggestions.adapter = wordSearchAdapter

        lifecycleScope.launch {
//             Collect word suggestions and update adapter
            viewModel.wordSuggestions.collectLatest { suggestions ->
                wordSearchAdapter.submitList(suggestions)
                binding.rcvWordSuggestions.visibility = if (suggestions.isNotEmpty()) View.VISIBLE else View.GONE
                binding.tvPageEmptyHint.visibility = if (suggestions.isEmpty()) View.VISIBLE else View.GONE
            }
            // Collect loading state
            viewModel.isLoading.collectLatest { isLoading ->
                binding.rcvWordSuggestions.visibility = if (isLoading) View.GONE else View.VISIBLE
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
            // Collect error state
            viewModel.error.collectLatest { error ->
                binding.rcvWordSuggestions.visibility = View.GONE
                binding.tvPageEmptyHint.visibility = View.VISIBLE
                binding.tvPageEmptyHint.text = error
            }
        }

        // Add text change listener for the search input
        binding.etSearch.addTextChangedListener { text ->
            if (text.toString().isNotEmpty()) {
                viewModel.fetchWordSuggestions(text.toString())
            }
        }

        return binding.root
    }
}

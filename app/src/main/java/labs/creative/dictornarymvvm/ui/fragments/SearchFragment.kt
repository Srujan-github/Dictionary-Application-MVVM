package labs.creative.dictornarymvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.ui.adapter.WordSearchAdapter
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var wordSearchAdapter: WordSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordSearchAdapter = WordSearchAdapter(emptyList())
        binding.rcvWordSuggestions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvWordSuggestions.adapter = wordSearchAdapter

        // Each flow gets its own launch{} — collectLatest suspends indefinitely,
        // so chaining them in one block means only the first ever executes.
        lifecycleScope.launch {
            viewModel.wordSuggestions.collectLatest { suggestions ->
                wordSearchAdapter.submitList(suggestions)
                binding.rcvWordSuggestions.visibility =
                    if (suggestions.isNotEmpty()) View.VISIBLE else View.GONE
                binding.tvPageEmptyHint.visibility =
                    if (suggestions.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                if (error != null) {
                    binding.rcvWordSuggestions.visibility = View.GONE
                    binding.tvPageEmptyHint.visibility = View.VISIBLE
                    binding.tvPageEmptyHint.text = error
                }
            }
        }

        binding.etSearch.addTextChangedListener { text ->
            if (text.toString().isNotEmpty()) {
                viewModel.fetchWordSuggestions(text.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

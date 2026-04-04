package labs.creative.dictornarymvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.ui.adapter.WordSearchAdapter
import labs.creative.dictornarymvvm.ui.viewmodel.SearchViewModel
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.FragmentSearchBinding

@AndroidEntryPoint
class SearchFragment : Fragment() {

    companion object {
        private const val TRANSITION_DURATION_MS = 300L
    }

    private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var wordSearchAdapter: WordSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Receive the shared element expansion from the Home screen search bar
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = TRANSITION_DURATION_MS
            scrimColor = android.graphics.Color.TRANSPARENT
        }
    }

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

        setupRecyclerView()
        observeViewModel()
        setupSearchInput()
        setupBackButton()

        // Auto-focus and show keyboard
        binding.etSearch.requestFocus()
        WindowInsetsControllerCompat(requireActivity().window, binding.etSearch)
            .show(WindowInsetsCompat.Type.ime())
    }

    private fun setupRecyclerView() {
        wordSearchAdapter = WordSearchAdapter { word ->
            val action = SearchFragmentDirections.actionSearchFragmentToResultFragment(word)
            findNavController().navigate(action)
        }
        binding.rcvWordSuggestions.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = wordSearchAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is labs.creative.dictornarymvvm.ui.viewmodel.SearchUiState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rcvWordSuggestions.visibility = View.GONE
                        binding.tvPageEmptyHint.visibility = View.GONE
                    }
                    is labs.creative.dictornarymvvm.ui.viewmodel.SearchUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.rcvWordSuggestions.visibility = View.GONE
                        binding.tvPageEmptyHint.visibility = View.GONE
                    }
                    is labs.creative.dictornarymvvm.ui.viewmodel.SearchUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvPageEmptyHint.visibility = View.GONE
                        binding.rcvWordSuggestions.visibility = View.VISIBLE
                        wordSearchAdapter.submitList(state.suggestions)
                    }
                    is labs.creative.dictornarymvvm.ui.viewmodel.SearchUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rcvWordSuggestions.visibility = View.GONE
                        binding.tvPageEmptyHint.visibility = View.VISIBLE
                        binding.tvPageEmptyHint.text = state.message
                    }
                }
            }
        }
    }

    private fun setupSearchInput() {
        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString()
            binding.clearButton.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
            viewModel.onSearchQueryChanged(query)
        }

        binding.clearButton.setOnClickListener {
            binding.etSearch.text?.clear()
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

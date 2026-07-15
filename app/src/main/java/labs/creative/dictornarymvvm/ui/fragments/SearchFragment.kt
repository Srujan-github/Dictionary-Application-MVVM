package labs.creative.dictornarymvvm.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.ui.adapter.WordSearchAdapter
import labs.creative.dictornarymvvm.ui.viewmodel.SearchViewModel
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.FragmentSearchBinding
import java.util.Locale

@AndroidEntryPoint
class SearchFragment : Fragment() {

    companion object {
        private const val TRANSITION_DURATION_MS = 300L
    }

    private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var wordSearchAdapter: WordSearchAdapter

    private val speechRecognizerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val binding = _binding ?: return@registerForActivityResult
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val spokenText = result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()
                if (!spokenText.isNullOrBlank()) {
                    binding.etSearch.setText(spokenText)
                    binding.etSearch.setSelection(spokenText.length)
                }
            }
        }

    private val recordAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val binding = _binding ?: return@registerForActivityResult
            if (granted) {
                launchSpeechRecognizer()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.voice_search_permission_rationale),
                    Snackbar.LENGTH_LONG,
                ).show()
            }
        }

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
        setupMicButton()

        // Auto-focus and show keyboard
        binding.etSearch.requestFocus()
        WindowInsetsControllerCompat(requireActivity().window, binding.etSearch)
            .show(WindowInsetsCompat.Type.ime())
    }

    private fun setupRecyclerView() {
        wordSearchAdapter = WordSearchAdapter { word ->
            if (findNavController().currentDestination?.id == R.id.searchFragment) {
                val action = SearchFragmentDirections.actionSearchFragmentToResultFragment(word)
                findNavController().navigate(action)
            }
        }
        binding.rcvWordSuggestions.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = wordSearchAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
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

    private fun setupMicButton() {
        binding.micButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    launchSpeechRecognizer()
                }
                else -> {
                    recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }

    private fun launchSpeechRecognizer() {
        val binding = _binding ?: return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            speechRecognizerLauncher.launch(intent)
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.voice_search_not_available),
                Snackbar.LENGTH_LONG,
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

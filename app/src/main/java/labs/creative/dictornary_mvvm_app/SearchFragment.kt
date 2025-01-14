package labs.creative.dictornary_mvvm_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornary_mvvm_app.databinding.FragmentSearchBinding
import labs.creative.dictornary_mvvm_app.ui.adapter.WordSearchAdapter
import javax.inject.Inject


class SearchFragment : Fragment() {
    private lateinit var viewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        binding.rcvWordSuggestions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        lifecycleScope.launch {
            viewModel.wordSuggestions.collectLatest { suggestions ->
                binding.rcvWordSuggestions.visibility = View.VISIBLE
                binding.rcvWordSuggestions.adapter = WordSearchAdapter(suggestions)
            }
            viewModel.isLoading.collectLatest { isLoading ->
                binding.rcvWordSuggestions.visibility = View.VISIBLE
                if (isLoading){
                    binding.progressBar.visibility = View.VISIBLE
                }else{
                    binding.progressBar.visibility = View.GONE
                }
            }
            viewModel.error.collectLatest { error ->
                binding.rcvWordSuggestions.visibility = View.GONE
                binding.tvPageEmptyHint.visibility = View.VISIBLE
                binding.tvPageEmptyHint.text = error
            }
        }

        binding.etSearch.addTextChangedListener { text ->
            if (text.toString().isNotEmpty()) {
                viewModel.fetchWordSuggestions(text.toString())
            }
        }
        return binding.root
    }
}
package labs.creative.dictornarymvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.ui.adapter.TrendingAdapter
import labs.creative.dictornarymvvm.ui.viewmodel.MainViewModel
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.FragmentMainBinding

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        private const val CHIP_STROKE_DIVISOR = 4
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var trendingAdapter: TrendingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTrendingList()
        observeViewModel()
        setupClickListeners()
        startTypeWriter()
    }

    private fun setupTrendingList() {
        trendingAdapter = TrendingAdapter { word ->
            navigateToResult(word)
        }
        binding.rcvTrending.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trendingAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.wordOfTheDay.collectLatest { wordInfo ->
                wordInfo ?: return@collectLatest
                binding.tvWotdWord.text = wordInfo.word
                binding.tvWotdPhonetic.text = wordInfo.phonetic
                binding.tvWotdDefinition.text = wordInfo.definitions.firstOrNull() ?: ""
            }
        }

        lifecycleScope.launch {
            viewModel.trendingWords.collectLatest { words ->
                trendingAdapter.submitList(words)
            }
        }

        lifecycleScope.launch {
            viewModel.recentWords.collectLatest { words ->
                populateRecentChips(words)
            }
        }
    }

    private fun populateRecentChips(words: List<String>) {
        binding.llRecentChips.removeAllViews()
        words.forEach { word ->
            val chip = Chip(requireContext()).apply {
                text = word
                isCheckable = false
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.color_surface)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text_primary))
                chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.color_divider)
                chipStrokeWidth = resources.getDimension(R.dimen.space_xs) / CHIP_STROKE_DIVISOR
                setOnClickListener { navigateToResult(word) }
            }
            val params = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).also { it.marginEnd = resources.getDimensionPixelSize(R.dimen.space_sm) }
            binding.llRecentChips.addView(chip, params)
        }
    }

    private fun setupClickListeners() {
        // Search bar tap → expand transition to SearchFragment
        binding.cardSearchBar.setOnClickListener {
            val extras = androidx.navigation.fragment.FragmentNavigatorExtras(
                binding.cardSearchBar to "shared_search_bar",
            )
            findNavController().navigate(
                R.id.action_mainFragment_to_searchFragment,
                null,
                null,
                extras,
            )
        }

        // Word of the Day arrow → navigate to ResultFragment
        binding.btnWotdArrow.setOnClickListener {
            val word = viewModel.wordOfTheDay.value?.word ?: return@setOnClickListener
            navigateToResult(word)
        }

        // WOTD card itself also navigable
        binding.cardWotd.setOnClickListener {
            val word = viewModel.wordOfTheDay.value?.word ?: return@setOnClickListener
            navigateToResult(word)
        }
    }

    private fun navigateToResult(word: String) {
        val action = MainFragmentDirections.actionMainFragmentToResultFragment(word)
        findNavController().navigate(action)
    }

    private fun startTypeWriter() {
        val taglines = listOf(
            getString(R.string.tagline_1),
            getString(R.string.tagline_2),
            getString(R.string.tagline_3),
        )
        binding.tagTypeWriter.animateTaglines(taglines)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

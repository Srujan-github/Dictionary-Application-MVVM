package labs.creative.dictornarymvvm.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.core.TtsManager
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.ui.viewmodel.ResultViewModel
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.FragmentResultBinding
import javax.inject.Inject

@AndroidEntryPoint
class ResultFragment : Fragment() {

    @Inject lateinit var ttsManager: TtsManager

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResultViewModel by viewModels()
    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ttsManager.init()
        setupClickListeners()
        observeViewModel()

        // Bug 3 fix: only fetch if not already loaded (ViewModel survives tab switches)
        if (viewModel.wordInfo.value == null) {
            viewModel.fetchWordInfo(args.word)
        }
    }

    private fun setupClickListeners() {
        binding.ibBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ibPronunciation.setOnClickListener {
            val word = viewModel.wordInfo.value?.word ?: return@setOnClickListener
            ttsManager.speak(word)  // TtsManager internally guards until ready
        }

        binding.ibShare.setOnClickListener {
            val wordInfo = viewModel.wordInfo.value ?: return@setOnClickListener
            val shareText = buildString {
                append(wordInfo.word)
                if (wordInfo.phonetic.isNotBlank()) append("\n${wordInfo.phonetic}")
                append("\n\n")
                wordInfo.definitions.firstOrNull()?.let { append(it) }
            }
            val intent = Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                },
                getString(R.string.share_via),
            )
            startActivity(intent)
        }

        // Bug 2 fix: wire bookmark button to toggle save
        binding.ibBookmark.setOnClickListener {
            viewModel.toggleSave()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.wordInfo.collectLatest { wordInfo ->
                wordInfo ?: return@collectLatest
                bindWordInfo(wordInfo)
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
                    binding.tvError.text = error
                    binding.tvError.visibility = View.VISIBLE
                } else {
                    binding.tvError.visibility = View.GONE
                }
            }
        }

        // Bug 2 fix: observe isSaved to update bookmark icon
        lifecycleScope.launch {
            viewModel.isSaved.collectLatest { saved ->
                val iconRes = if (saved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline
                binding.ibBookmark.setImageResource(iconRes)
            }
        }
    }

    private fun bindWordInfo(info: WordInfo) {
        binding.tvWord.text = info.word
        binding.tvPhonetic.text = info.phonetic.ifBlank { "" }
        binding.chipPartOfSpeech.text = info.partOfSpeech.replaceFirstChar { it.uppercase() }
        binding.chipPartOfSpeech.visibility =
            if (info.partOfSpeech.isNotBlank()) View.VISIBLE else View.GONE

        // Definition
        binding.tvDefinition.text = info.definitions.firstOrNull()
            ?: getString(R.string.word_not_found)

        // Examples
        binding.llExamples.removeAllViews()
        if (info.examples.isNotEmpty()) {
            binding.tvExamplesLabel.visibility = View.VISIBLE
            info.examples.forEach { example ->
                val card = buildExampleCard(example)
                binding.llExamples.addView(card)
            }
        } else {
            binding.tvExamplesLabel.visibility = View.GONE
        }

        // Synonyms
        buildChips(
            info.synonyms,
            binding.chipGroupSynonyms,
            binding.tvSynonymsLabel,
            bgColor = R.color.color_synonym_bg,
            textColor = R.color.color_synonym_text
        )

        // Antonyms
        buildChips(
            info.antonyms,
            binding.chipGroupAntonyms,
            binding.tvAntonymsLabel,
            bgColor = R.color.color_antonym_bg,
            textColor = R.color.color_antonym_text
        )
    }

    private fun buildExampleCard(example: String): MaterialCardView {
        val card = MaterialCardView(requireContext()).apply {
            radius = resources.getDimension(R.dimen.corner_card_md)
            cardElevation = resources.getDimension(R.dimen.elevation_card)
            setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_surface))
            val lp = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).also { it.bottomMargin = resources.getDimensionPixelSize(R.dimen.space_sm) }
            layoutParams = lp
        }
        val tv = android.widget.TextView(requireContext()).apply {
            text = "❝  $example"
            textSize = 14f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text_secondary))
            setPadding(
                resources.getDimensionPixelSize(R.dimen.space_md),
                resources.getDimensionPixelSize(R.dimen.space_md),
                resources.getDimensionPixelSize(R.dimen.space_md),
                resources.getDimensionPixelSize(R.dimen.space_md),
            )
        }
        card.addView(tv)
        return card
    }

    private fun buildChips(
        words: List<String>,
        chipGroup: com.google.android.material.chip.ChipGroup,
        label: View,
        bgColor: Int,
        textColor: Int,
    ) {
        chipGroup.removeAllViews()
        if (words.isNotEmpty()) {
            label.visibility = View.VISIBLE
            words.forEach { word ->
                val chip = Chip(requireContext()).apply {
                    text = word
                    isCheckable = false
                    chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), bgColor)
                    setTextColor(ContextCompat.getColor(requireContext(), textColor))
                    setOnClickListener {
                        val action = ResultFragmentDirections
                            .actionResultFragmentToResultFragment(word)
                        findNavController().navigate(action)
                    }
                }
                chipGroup.addView(chip)
            }
        } else {
            label.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // TtsManager is a Singleton — do NOT shut it down here
    }
}

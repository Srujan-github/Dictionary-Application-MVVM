package labs.creative.dictornarymvvm.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.ui.viewmodel.SettingsViewModel
import labs.creative.dictornarymvvmapp.BuildConfig
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.FragmentSettingsBinding

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        lifecycleScope.launch {
            viewModel.isDarkModeEnabled.collectLatest { enabled ->
                binding.switchDarkMode.isChecked = enabled
            }
        }
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDarkMode(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO,
            )
        }

        lifecycleScope.launch {
            viewModel.isDailyWordEnabled.collectLatest { enabled ->
                binding.switchDailyWord.isChecked = enabled
            }
        }
        binding.switchDailyWord.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDailyWord(isChecked)
        }

        binding.rowPrivacy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/privacy")))
        }
        binding.rowFeedback.setOnClickListener {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:feedback@lexicon.app")))
        }
        binding.rowRate.setOnClickListener {
            val uri = Uri.parse("market://details?id=${requireContext().packageName}")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

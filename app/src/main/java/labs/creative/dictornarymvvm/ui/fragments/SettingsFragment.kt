package labs.creative.dictornarymvvm.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.core.DailyWordWorker
import labs.creative.dictornarymvvm.ui.viewmodel.SettingsViewModel
import labs.creative.dictornarymvvmapp.BuildConfig
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.FragmentSettingsBinding
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    private var isUpdatingDarkMode = false
    private var isUpdatingDailyWord = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isDarkModeEnabled.collectLatest { enabled ->
                isUpdatingDarkMode = true
                binding.switchDarkMode.isChecked = enabled
                isUpdatingDarkMode = false
            }
        }
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingDarkMode) return@setOnCheckedChangeListener
            // 1. Persist the preference
            viewModel.toggleDarkMode(isChecked)
            // 2. Apply via the global delegate so all windows update.
            //    MainActivity reads this same preference via setDefaultNightMode on next cold start.
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO,
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isDailyWordEnabled.collectLatest { enabled ->
                isUpdatingDailyWord = true
                binding.switchDailyWord.isChecked = enabled
                isUpdatingDailyWord = false
                // Keep the scheduled work in sync with the persisted preference,
                // including on initial load (e.g. after a process restart).
                updateDailyWordSchedule(enabled)
            }
        }
        binding.switchDailyWord.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingDailyWord) return@setOnCheckedChangeListener
            viewModel.toggleDailyWord(isChecked)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.appLanguage.collectLatest { language ->
                binding.tvLanguageValue.text = language
            }
        }
        binding.rowLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.rowAbout.setOnClickListener {
            showAboutDialog()
        }
        binding.rowPrivacy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url))))
        }
        binding.rowFeedback.setOnClickListener {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.feedback_email))))
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

    private fun updateDailyWordSchedule(enabled: Boolean) {
        val workManager = WorkManager.getInstance(requireContext())
        if (enabled) {
            val request = PeriodicWorkRequestBuilder<DailyWordWorker>(1, TimeUnit.DAYS).build()
            workManager.enqueueUniquePeriodicWork(
                DAILY_WORD_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        } else {
            workManager.cancelUniqueWork(DAILY_WORD_WORK_NAME)
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(getString(R.string.language_value))
        val checkedItem = languages.indexOf(viewModel.appLanguage.value).coerceAtLeast(0)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_language)
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                viewModel.setAppLanguage(languages[which])
                dialog.dismiss()
            }
            .show()
    }

    private fun showAboutDialog() {
        val message = getString(R.string.app_version, BuildConfig.VERSION_NAME) + "\n\n" + getString(R.string.tagline_1)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.app_name)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    companion object {
        private const val DAILY_WORD_WORK_NAME = "daily_word_work"
    }
}

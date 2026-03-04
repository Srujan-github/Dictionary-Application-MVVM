package labs.creative.dictornarymvvm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.data.preferences.UserPreferencesRepository
import labs.creative.dictornarymvvmapp.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        applyDarkModeAsync(savedInstanceState)
    }

    /**
     * Reads the dark-mode preference off the main thread via a coroutine and applies it.
     *
     * We use [delegate.localNightMode] so only this Activity is affected (no global state side
     * effects). On the very first cold start, if the stored preference differs from the default
     * (FOLLOW_SYSTEM), we call [recreate] once – this is instantaneous before the user has
     * interacted with anything. A [savedInstanceState] check prevents infinite recreate loops.
     */
    private fun applyDarkModeAsync(savedInstanceState: Bundle?) {
        // Only apply on first create, not on config-change/recreate
        if (savedInstanceState != null) return

        lifecycleScope.launch {
            val isDark = userPreferencesRepository.isDarkModeEnabled.first()
            val targetMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                             else AppCompatDelegate.MODE_NIGHT_NO
            if (delegate.localNightMode != targetMode) {
                delegate.localNightMode = targetMode
                // recreate() applies the new theme instantly before user interaction
                recreate()
            }
        }
    }
}

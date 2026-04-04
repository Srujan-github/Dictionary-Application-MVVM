package labs.creative.dictornarymvvm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import labs.creative.dictornarymvvm.data.preferences.UserPreferencesRepository
import labs.creative.dictornarymvvmapp.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. super.onCreate() MUST come first – Hilt injects @Inject fields here.
        super.onCreate(savedInstanceState)

        // 2. Apply the saved dark-mode preference BEFORE setContentView() so the
        //    correct theme colours are used on the very first layout pass.
        //    We only do this on a true cold start (savedInstanceState == null) to
        //    avoid an infinite recreate loop if the mode change triggers a config change.
        if (savedInstanceState == null) {
            applyDarkMode()
        }

        // 3. Inflate and show the layout with the correct theme already applied.
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)
    }

    /**
     * Reads the dark-mode preference synchronously (DataStore on-disk, ~1 ms) and
     * applies it via [AppCompatDelegate.setDefaultNightMode].
     *
     * Called after [super.onCreate] so Hilt has already injected
     * [userPreferencesRepository], and before [setContentView] so the window is
     * inflated with the correct theme on the first pass — no visual flash.
     */
    private fun applyDarkMode() {
        val isDark = runBlocking { userPreferencesRepository.isDarkModeEnabled.first() }
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO,
        )
    }
}

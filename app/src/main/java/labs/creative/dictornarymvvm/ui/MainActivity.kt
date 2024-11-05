package labs.creative.dictornarymvvm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.ActivityMainBinding


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
private lateinit var _binding : ActivityMainBinding
private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater,null,false)
        setContentView(binding.root)
    }
}

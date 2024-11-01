package labs.creative.dictornary_mvvm_app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: WordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchInput = findViewById<EditText>(R.id.searchInput)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val loadingIndicator = findViewById<ProgressBar>(R.id.loadingIndicator)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)

        searchButton.setOnClickListener {
            val word = searchInput.text.toString()
            viewModel.fetchWordSuggestions(word)
//            viewModel.fetchWordInfo(word)
        }


        lifecycleScope.launch {
            viewModel.wordSuggestions.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        loadingIndicator.visibility = View.VISIBLE
                        resultTextView.visibility = View.GONE
                        errorTextView.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        loadingIndicator.visibility = View.GONE
                        resultTextView.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        resource.data?.let { suggestions ->
                            resultTextView.text = "Suggestions:\n" +
                                    suggestions.joinToString("\n") { it.word }
                        }
                    }
                    is Resource.Error -> {
                        loadingIndicator.visibility = View.GONE
                        resultTextView.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = resource.errorMessage
                    }
                }
            }
        }


        lifecycleScope.launch {
            viewModel.wordInfo.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        loadingIndicator.visibility = View.VISIBLE
                        resultTextView.visibility = View.GONE
                        errorTextView.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        loadingIndicator.visibility = View.GONE
                        resultTextView.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        resource.data?.let { wordInfoList ->
                            resultTextView.append("\n\nWord Info:\n")
                            wordInfoList.forEach { wordInfo ->
                                resultTextView.append("Word: ${wordInfo.word}\n")
                                wordInfo.meanings.forEach { meaning ->
                                    resultTextView.append("Meaning: $meaning\n")
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        loadingIndicator.visibility = View.GONE
                        resultTextView.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = resource.errorMessage
                    }
                }
            }
        }
    }
}

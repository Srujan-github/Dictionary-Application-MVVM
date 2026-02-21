package labs.creative.dictornarymvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import labs.creative.dictornarymvvm.ui.adapter.WordInfo
import labs.creative.dictornarymvvm.ui.adapter.WordsTilesAdapter
import labs.creative.dictornarymvvmapp.R
import labs.creative.dictornarymvvmapp.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private lateinit var navController: NavController
    private val binding get() = _binding!!

    private val wordInfoList = listOf(
        WordInfo("Serendipity", "The occurrence of events by chance in a happy or beneficial way."),
        WordInfo("Ephemeral", "Lasting for a very short time."),
        WordInfo("Euphoria", "A feeling or state of intense excitement and happiness."),
        WordInfo("Quintessential", "Representing the most perfect or typical example of a quality or class."),
        WordInfo("Ineffable", "Too great or extreme to be expressed or described in words."),
        WordInfo("Solitude", "The state or situation of being alone."),
        WordInfo("Resilience", "The capacity to recover quickly from difficulties; toughness."),
        WordInfo("Halcyon", "Denoting a period of time in the past that was idyllically happy and peaceful."),
        WordInfo(
            "Aurora",
            "A natural electrical phenomenon characterized by the appearance of streamers " +
                "of reddish or greenish light in the sky.",
        ),
        WordInfo("Ethereal", "Extremely delicate and light in a way that seems not of this world."),
    )

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
        navController = Navigation.findNavController(view)
        binding.searchButton.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_searchFragment)
        }
        initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initialize() {
        binding.rcvSuggestions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcvSuggestions.adapter = WordsTilesAdapter(wordInfoList)
    }
}

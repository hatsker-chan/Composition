package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level

class GameFragment : Fragment() {

    private lateinit var level: Level

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSum.setOnClickListener{
            val gameResult = GameResult(true, 10, 15, GameSettings(1, 1, 1, 1))
            launchGameFinishedFragment(gameResult)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArgs(){
        level = requireArguments().getSerializable(KEY_LEVEL) as Level
    }

    private fun launchGameFinishedFragment(gameResult: GameResult){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    companion object{
        const val NAME = "GameFragment"
        private const val KEY_LEVEL = "level"

        fun newInstance(level: Level): GameFragment{
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_LEVEL, level)
                }
            }
        }
    }
}
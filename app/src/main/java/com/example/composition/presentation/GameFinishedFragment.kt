package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding

class GameFinishedFragment : Fragment() {

    private val args by navArgs<GameFinishedFragmentArgs>()

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emojiRes = if (args.gameResult.winner) {
            R.drawable.emoji_happy
        } else {
            R.drawable.emoji_sad
        }
        binding.ivEmoji.setImageResource(emojiRes)

        binding.tvMinPercent.text = String.format(
            requireContext().resources.getString(R.string.min_percent),
            args.gameResult.gameSettings.minPercentOfRightAnswers
        )
        binding.tvMinScore.text = String.format(
            requireContext().resources.getString(R.string.min_rights_answers),
            args.gameResult.gameSettings.minCountOfRightAnswers
        )
        binding.tvPlayerPercent.text = String.format(
            requireContext().resources.getString(R.string.player_percent),
            args.gameResult.percentOfRightAnswers
        )
        binding.tvPlayerScore.text = String.format(
            requireContext().resources.getString(R.string.players_score),
            args.gameResult.countOfRightAnswers
        )

        binding.buttonTryAgain.setOnClickListener {
            retryGame()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun retryGame() {
        findNavController().popBackStack()
    }
}
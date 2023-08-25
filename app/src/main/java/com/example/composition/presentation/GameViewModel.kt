package com.example.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {

    private val repository = GameRepositoryImpl

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private lateinit var gameSettings: GameSettings

    private var timer: CountDownTimer? = null
    private var countOfRightAnswers = 0
    private var countOfQuestion = 0

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private fun getNextQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun startGame() {
        getGameSettings()
        startTimer()
        getNextQuestion()
        updateProgress()
    }

    init {
        startGame()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        getNextQuestion()
        updateProgress()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestion++
    }

    private fun getGameSettings() {
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun updateProgress() {
        val percent = calculatePercent()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.percent_of_right_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCount.value =
            countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercent.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercent(): Int {
        if (countOfQuestion == 0){
            return 0
        }
        return ((countOfRightAnswers.toDouble()/ countOfQuestion.toDouble()) * 100).toInt()
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            _enoughCount.value == true && _enoughPercent.value == true,
            countOfRightAnswers,
            countOfQuestion,
            gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        const val MILLIS_IN_SECONDS = 1000L
        const val SECONDS_IN_MINUTES = 60
    }
}
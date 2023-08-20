package com.example.composition.domain.entity

class GameResult (
    val winner: Boolean,
    val countOfRightAnswers: Int,
    val countOnQuestions: Int,
    val gameSettings: GameSettings
)
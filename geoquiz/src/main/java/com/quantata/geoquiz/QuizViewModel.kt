package com.quantata.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
class QuizViewModel : ViewModel() {
    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )
    var currentIndex = 0
//    var isCheater = false

    val isCheater: Boolean
        get() = questionBank[currentIndex].isCheated

    val totalCount = questionBank.size
    var answeredCount = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId
    val currentQuestionIsCorrect: Boolean
        get() = questionBank[currentIndex].isCorrect
    val currentQuestionIsAnswered: Boolean
        get() = questionBank[currentIndex].isAnswered
    var resultText = ""

    fun setIsCorrect(isCorrect: Boolean) {
        questionBank[currentIndex].isCorrect = isCorrect
    }
    fun setIsAnswered(isAnswered: Boolean) {
        questionBank[currentIndex].isAnswered = isAnswered
    }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size // questionBank.size = 6, currentIndex = 0 일때 다음은 1번째이므로 1%6 = 1, cntIdx = 5(마지막)일땐 다음은 0 번째로
    }

    fun moveToPrevious() {
        currentIndex = (if(currentIndex == 0) 5 else (currentIndex - 1)) % questionBank.size // questionBank.size = 6, currentIndex = 0 일때 다음은 1번째이므로 1%6 = 1, cntIdx = 5(마지막)일땐 다음은 0 번째로
    }

    fun setIsCheated(isCheated: Boolean) {
        questionBank[currentIndex].isCheated = isCheated
    }
}
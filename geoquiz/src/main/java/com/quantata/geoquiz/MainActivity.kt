package com.quantata.geoquiz

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.quantata.geoquiz.databinding.ActivityMainBinding
import java.lang.Math.round
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )
    private var currentIndex = 0

    private val totalCount = questionBank.size
    private var answeredCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTrue.setOnClickListener {
            checkAnswer(true)
        }

        binding.btnFalse.setOnClickListener {
            checkAnswer(false)
        }

        // 현재 index 가 가르키는 질문 텍스트를 TextView 에 설정
        updateQuestion()

        binding.questionTextView.setOnClickListener {
            getQuestion(true)
        }

        binding.btnNext.setOnClickListener{
            getQuestion(true)
        }

        binding.btnPrevious.setOnClickListener{
            getQuestion(false)
        }
    }

    private fun getQuestion(isNext: Boolean) {
        currentIndex =
            if(isNext)
                (currentIndex + 1) % questionBank.size // questionBank.size = 6, currentIndex = 0 일때 다음은 1번째이므로 1%6 = 1, cntIdx = 5(마지막)일땐 다음은 0 번째로
            else
                (if(currentIndex == 0) 5 else (currentIndex - 1)) % questionBank.size // questionBank.size = 6, currentIndex = 0 일때 다음은 1번째이므로 1%6 = 1, cntIdx = 5(마지막)일땐 다음은 0 번째로

        updateQuestion()
        setBtnAvailable()
    }

    /**
     * 현재 질문의 isDone 상태에 따라 Btn clickable 설정
     */
    private fun setBtnAvailable() {
        binding.btnTrue.isClickable = !questionBank[currentIndex].isCorrect
        binding.btnFalse.isClickable = !questionBank[currentIndex].isCorrect
    }

    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        binding.questionTextView.setText(questionTextResId) // 초기에는 currentIndex 가 0 이니 0번째 값을 설정
    }

    private fun checkAnswer(userAnswer: Boolean) {
        if(!questionBank[currentIndex].isAnswered) // 답을 안했을 경우에만 answeredCount 올림
            answeredCount += 1
        questionBank[currentIndex].isAnswered = true

        val correctAnswer = questionBank[currentIndex].answer

        val messageResId = if(userAnswer == correctAnswer) {
            questionBank[currentIndex].isCorrect = true // 답을 맞췄을때 Clickable = false
            R.string.correct_toast
        } else {
            questionBank[currentIndex].isCorrect = false
            R.string.incorrect_toast
        }

        val toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            toast.setGravity(Gravity.TOP, 0, 0) // SDK 30 부터는 사용하지 못함
        }
        toast.show()

        setBtnAvailable()

        // 모든 질문에 대답했는지 확인
        if(totalCount == answeredCount) {
            binding.questionTextView.visibility = View.GONE
            binding.resultTextView.visibility = View.VISIBLE

            var correctAnswer = 0.00
            for(question in questionBank) {
                correctAnswer += if(question.isCorrect) 1 else 0
            }
            binding.resultTextView.text = "정답율: ${((correctAnswer / totalCount) * 100).roundToInt()}%"
        }
    }
}
package com.quantata.geoquiz

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.quantata.geoquiz.databinding.ActivityMainBinding

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
            showNextQuestion()
        }

        binding.btnNext.setOnClickListener{
            showNextQuestion()
        }

        binding.btnPrevious.setOnClickListener{
            showPreviousQuesition()
        }
    }

    private fun showNextQuestion() {
        currentIndex = (currentIndex + 1) % questionBank.size // questionBank.size = 6, currentIndex = 0 일때 다음은 1번째이므로 1%6 = 1, cntIdx = 5(마지막)일땐 다음은 0 번째로
        updateQuestion()
    }

    private fun showPreviousQuesition() {
        currentIndex = (if(currentIndex == 0) 5 else (currentIndex - 1)) % questionBank.size // questionBank.size = 6, currentIndex = 0 일때 다음은 1번째이므로 1%6 = 1, cntIdx = 5(마지막)일땐 다음은 0 번째로
        updateQuestion()
    }

    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        binding.questionTextView.setText(questionTextResId) // 초기에는 currentIndex 가 0 이니 0번째 값을 설정
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questionBank[currentIndex].answer

        val messageResId = if(userAnswer == correctAnswer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        val toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            toast.setGravity(Gravity.TOP, 0, 0) // SDK 30 부터는 사용하지 못함
        }
        toast.show()
    }
}
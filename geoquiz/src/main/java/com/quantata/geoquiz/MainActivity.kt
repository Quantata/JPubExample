package com.quantata.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quantata.geoquiz.databinding.ActivityMainBinding
import java.lang.Math.round
import kotlin.math.roundToInt

private const val KEY_INDEX = "index"
private const val TAG = "MainActivity"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if(quizViewModel.currentIndex == 0)
         quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0

        binding.btnTrue.setOnClickListener {
            checkAnswer(true)
        }

        binding.btnFalse.setOnClickListener {
            checkAnswer(false)
        }

        if(quizViewModel.totalCount != quizViewModel.answeredCount)
            updateQuestion() // 현재 index 가 가르키는 질문 텍스트를 TextView 에 설정
        else
            setResult()

        binding.questionTextView.setOnClickListener {
            getQuestion(true)
        }

        binding.btnNext.setOnClickListener{
            getQuestion(true)
        }

        binding.btnPrevious.setOnClickListener{
            getQuestion(false)
        }

        binding.btnCheat.setOnClickListener {
            // CheatActivity 시작
//            val intent = Intent(this, CheatActivity::class.java)
//            startActivity(intent)
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
//            startActivity(intent)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }
    }

    private fun getQuestion(isNext: Boolean) {
            if(isNext)
                quizViewModel.moveToNext()
            else
                quizViewModel.moveToPrevious()
        updateQuestion()
        setBtnAvailable()
    }

    /**
     * 현재 질문의 isDone 상태에 따라 Btn clickable 설정
     */
    private fun setBtnAvailable() {
        binding.btnTrue.isClickable = !quizViewModel.currentQuestionIsCorrect
        binding.btnFalse.isClickable = !quizViewModel.currentQuestionIsCorrect
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId) // 초기에는 currentIndex 가 0 이니 0번째 값을 설정
    }

    private fun checkAnswer(userAnswer: Boolean) {
        if(!quizViewModel.currentQuestionIsAnswered) // 답을 안했을 경우에만 answeredCount 올림 -> 했을때는 이미 count 올렸을테니까
            quizViewModel.answeredCount += 1
        quizViewModel.setIsAnswered(true)

//        val messageResId = if(userAnswer == quizViewModel.currentQuestionAnswer) {
//            quizViewModel.setIsCorrect(true) // 답을 맞췄을때 Clickable = false
//            R.string.correct_toast
//        } else {
//            quizViewModel.setIsCorrect(false)
//            R.string.incorrect_toast
//        }

        val correctAnswer: Boolean = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.isCheater -> {
                R.string.judgement_toast
            }
            userAnswer == correctAnswer -> {
                quizViewModel.setIsCorrect(true) // 답을 맞췄을때 Clickable = false
                R.string.correct_toast
            }
            else -> {
                quizViewModel.setIsCorrect(false)
                R.string.incorrect_toast
            }
        }

        val toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            toast.setGravity(Gravity.TOP, 0, 0) // SDK 30 부터는 사용하지 못함
        }
        toast.show()

        setBtnAvailable()
        if(quizViewModel.totalCount == quizViewModel.answeredCount)
            setResult()

    }

    private fun setResult() {
        // 모든 질문에 대답했는지 확인
        binding.questionTextView.visibility = View.GONE
        binding.resultTextView.visibility = View.VISIBLE

        var correctAnswer = 0.00
        for(question in quizViewModel.questionBank) {
            correctAnswer += if(question.isCorrect) 1 else 0
        }
        quizViewModel.resultText = "정답율: ${((correctAnswer / quizViewModel.totalCount) * 100).roundToInt()}%"
        binding.resultTextView.text = quizViewModel.resultText
    }

    /**
     * 앱이 백그라운드에서 프로세스에 의해 종료되었을 경우 어떤 오버라이드 함수도 호출되지 않기 때문에
     * 일시적으로 액티비티 외부에 저장하는 데이터인 onSaveInstanceState를 오버라이드하여 데이터 저장
     *
     * 백그라운드에서 호출되어 지워졌다해도 onSaveInstanceState는 onPause() 즉, 중단상태에서
     * 호출되기 때문에 이미 데이터는 저장되어 있음.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    /**
     * requestCode : MainActivity 에서 요청한 코드
     * resultCode : SubActivity 에서 보낸 코드
     * data : SubActivity 에서 보낸 data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK) {
            return
        }

        if(requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.setIsCheated(data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false)
//            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
}
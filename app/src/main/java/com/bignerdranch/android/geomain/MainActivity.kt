@file:Suppress("DEPRECATION")

package com.bignerdranch.android.geomain

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX1 = "currentIndex"
private const val KEY_INDEX2 = "correctChek"
private const val KEY_INDEX4 = "currentCheater"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var currentCheat: TextView
    private val quizViewModel: QuizViewModel by
    lazy {
        ViewModelProviders.of(this)[QuizViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX1, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        val correctChek = savedInstanceState?.getInt(KEY_INDEX2, 0) ?: 0
        quizViewModel.correctChek = correctChek
        val currentCheater = savedInstanceState?.getInt(KEY_INDEX4, 0) ?: 0
        quizViewModel.currentCheater = currentCheater

        questionTextView = findViewById(R.id.question_text_view)

        nextButton = findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        trueButton = findViewById(R.id.true_button)
        trueButton.setOnClickListener {
                view: View -> checkAnswer(true)
        }

        falseButton = findViewById(R.id.false_button)
        falseButton.setOnClickListener {
                view: View -> checkAnswer(false)
        }

        cheatButton = findViewById(R.id.cheat_button)
        cheatButton.setOnClickListener {
            if (quizViewModel.currentCheater < 3)
            {
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
                quizViewModel.cheaterUpdate()
            }
            else {
                Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT).show()
            }
            cheatButton.visibility = View.INVISIBLE
        }

        currentCheat = findViewById(R.id.current_cheat)
        val str = "У вас осталось" + (3-quizViewModel.currentCheater) + "подсказок из 3"
        currentCheat.setText(str)

        updateQuestion()
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT)
        {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,
            "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG,
            "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG,
            "onPause() called")
    }
    override fun
            onSaveInstanceState(savedInstanceState: Bundle)
    {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX1, quizViewModel.currentIndex)
        savedInstanceState.putInt(KEY_INDEX2, quizViewModel.correctChek)
        savedInstanceState.putInt(KEY_INDEX4, quizViewModel.currentCheater)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy() called")
    }
    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        nextButton.visibility = View.INVISIBLE
        trueButton.visibility = View.VISIBLE
        falseButton.visibility = View.VISIBLE
        cheatButton.visibility = View.VISIBLE
        quizViewModel.isCheater = false
        val str = "У вас осталось " + (3-quizViewModel.currentCheater) + " подсказок из 3"
        currentCheat.setText(str)
    }
    private fun checkAnswer(userAnswer: Boolean) {
        nextButton.visibility = View.VISIBLE
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = if (userAnswer == correctAnswer)
        {
            quizViewModel.correctUpdate()
            R.string.correct_toast
        }
        else
            R.string.incorrect_toast

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        trueButton.visibility = View.INVISIBLE
        falseButton.visibility = View.INVISIBLE
        cheatButton.visibility = View.INVISIBLE

        if (quizViewModel.currentIndex == quizViewModel.questionBank.size-1){
            nextButton.visibility = View.INVISIBLE
            showCustomDialog(quizViewModel.correctChek.toString())
        }

    }
    private fun showCustomDialog(data: String) {
        val dialogBinding = layoutInflater.inflate(R.layout.activity_modal, null)
        val myDialog = Dialog(this)
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(true)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()
        val resultText = myDialog.findViewById<TextView>(R.id.result_text)
        val str = "Вы ответили правильно на " + data + " вопросов из " + quizViewModel.questionBank.size
        resultText.text = str
        val okButton = dialogBinding.findViewById<Button>(R.id.ok_button)
        okButton.setOnClickListener {
            myDialog.dismiss()
        }
    }

}
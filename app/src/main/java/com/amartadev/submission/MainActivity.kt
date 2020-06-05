package com.amartadev.submission

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {
    private var score = 0
    private var gameStarted = false

    private lateinit var countDownTimer: CountDownTimer
    private val initialCountDown: Long = 6000
    private val countDownInterval: Long = 1000
    private var timeLeftOnTimer: Long = 6000

    private lateinit var buttonTapMe: Button
    private lateinit var textviewScore: TextView
    private lateinit var textviewLeftTime: TextView

    companion object{
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY= "TIME_LEFT_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate called. Score is: $score" )

        buttonTapMe = findViewById(R.id.button_tapme)
        textviewScore = findViewById(R.id.ScoreTextView)
        textviewLeftTime = findViewById(R.id.LeftTimeTextView)

        buttonTapMe.setOnClickListener {view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }
        if (savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer =  savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        }else{
            resetGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionAbout){
            showInfo()
        }
        return true
    }

    private fun showInfo(){
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun restoreGame() {
        textviewScore.text = getString(R.string.your_score, score)

        val restoredTime = timeLeftOnTimer / 1000
        textviewLeftTime.text = getString(R.string.left_time, restoredTime)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                textviewLeftTime.text = getString(R.string.left_time, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        countDownTimer.start()
        gameStarted = true
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()

        Log.d(TAG, "onSavedInstancesState: Saving Score: $score & Time Left: $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }
    private fun resetGame(){
        score = 0

        textviewScore.text = getString(R.string.your_score, score)

        val initialLeftTime = initialCountDown / 1000
        textviewLeftTime.text = getString(R.string.left_time, initialLeftTime)

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                textviewLeftTime.text = getString(R.string.left_time, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarted = false
    }

    private fun incrementScore() {
        if (!gameStarted){
            startGame()
        }
        score += 1
        val newScore = getString (R.string.your_score)
        textviewScore.text = newScore

        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        textviewScore.startAnimation(blinkAnimation)
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.gameOverMessage, score), Toast.LENGTH_LONG).show()
        resetGame()
    }
}
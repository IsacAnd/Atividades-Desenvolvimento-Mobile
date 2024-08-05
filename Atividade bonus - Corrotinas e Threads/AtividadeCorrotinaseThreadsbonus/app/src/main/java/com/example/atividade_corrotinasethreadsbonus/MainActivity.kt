package com.example.atividade_corrotinasethreadsbonus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton: Button = findViewById(R.id.startButton)
        val inputTime: EditText = findViewById(R.id.inputTime)

        startButton.setOnClickListener {
            val timeInSeconds = inputTime.text.toString().toIntOrNull() ?: return@setOnClickListener
            startTimer(timeInSeconds)
        }
    }

    private fun startTimer(timeInSeconds: Int) {

        val timerText: TextView = findViewById(R.id.timerText)

        lifecycleScope.launch {
            for (i in timeInSeconds downTo 0) {
                timerText.text = i.toString()
                delay(1000)
            }
            showAlarmScreen()
        }
    }

    private fun showAlarmScreen() {
        val intent = Intent(this, AlarmActivity::class.java)
        startActivity(intent)
    }
}

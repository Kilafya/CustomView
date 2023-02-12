package com.drayl.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.drayl.customview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isFirstPlayer: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        updateText()
        binding.ticTacToeView.ticTacToeField = TicTacToeField(10, 10)
        binding.ticTacToeView.actionListener = { row, column, field ->
            if (field.getCell(row, column) != Cell.EMPTY) {
                Toast.makeText(this, "This field is already busy", Toast.LENGTH_LONG).show()
            } else {
                val cell = if (isFirstPlayer) Cell.PLAYER_1 else Cell.PLAYER_2
                isFirstPlayer = !isFirstPlayer
                field.setCell(row, column, cell)
            }
        }
    }

    private fun updateText() {
        binding.stepHint.text = String.format(
            getString(R.string.step_hint_format),
            if (isFirstPlayer) 1 else 2
        )
    }

}
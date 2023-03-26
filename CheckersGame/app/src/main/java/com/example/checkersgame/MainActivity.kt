package com.example.checkersgame

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import controller.CheckersController
import model.CheckersModel
import view.CheckersBoardView
import android.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var checkersBoardView: CheckersBoardView
    private lateinit var checkersController: CheckersController
    private lateinit var scoreTextView: TextView
    private lateinit var resetButton: Button
    private lateinit var turnTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        scoreTextView = findViewById(R.id.scoreTextView)
        resetButton = findViewById(R.id.resetButton)
        turnTextView = findViewById(R.id.turnTextView)

        // Instantiate CheckersBoardView and CheckersController
        val checkersModel = CheckersModel()
        checkersBoardView = CheckersBoardView(this)
        checkersController = CheckersController(checkersModel, checkersBoardView)
        // Set up communication between CheckersBoardView and CheckersController
        checkersBoardView.checkersModel = checkersModel
        checkersBoardView.onSquareTappedListener = { row, col ->
            checkersController.onSquareTapped(row, col)
            updateScore()
            updateTurn()
            if (checkersController.checkersModel.isGameOver()) {
                showGameOverDialog()
            }
        }

        // Set up UI components
        resetButton.setOnClickListener {
            checkersController.resetGame()
            updateScore()
            updateTurn()
        }

        // Add CheckersBoardView to the activity's layout
        checkersBoardView.id = View.generateViewId()
        constraintLayout.addView(checkersBoardView)

        // Set constraints for the CheckersBoardView
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(
            checkersBoardView.id,
            ConstraintSet.TOP,
            resetButton.id,
            ConstraintSet.BOTTOM,
            16
        )
        constraintSet.connect(
            checkersBoardView.id,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM
        )
        constraintSet.connect(
            checkersBoardView.id,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START
        )
        constraintSet.connect(
            checkersBoardView.id,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END
        )
        constraintSet.applyTo(constraintLayout)

        // Update the score display initially
        updateScore()
        updateTurn()
    }

    private fun updateScore() {
        val redScore = checkersController.checkersModel.redPlayerScore
        val blackScore = checkersController.checkersModel.blackPlayerScore
        scoreTextView.text = getString(R.string.score_format, redScore, blackScore)
    }

    private fun updateTurn() {
        val currentPlayer = checkersController.checkersModel.currentPlayer
        val playerName = if (currentPlayer == CheckersModel.Player.RED) "Red" else "Black"
        turnTextView.text = getString(R.string.turn_format, playerName)
    }

    private fun showGameOverDialog() {
        val winner = checkersController.checkersModel.currentPlayer
        val winnerName = if (winner == CheckersModel.Player.RED) "Red" else "Black"
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over")
        builder.setMessage("$winnerName player wins!")
        builder.setPositiveButton("Restart") { _, _ ->
            checkersController.resetGame()
            updateScore()
            updateTurn()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}



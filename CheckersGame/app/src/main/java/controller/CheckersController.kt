package controller

import model.CheckersModel
import view.CheckersBoardView

class CheckersController(
    val checkersModel: CheckersModel,
    private val checkersBoardView: CheckersBoardView,
    private var mustCapture: Boolean = false

) {
    private val boardSize = 8
    private var selectedRow: Int? = null
    private var selectedCol: Int? = null
    var gameOverListener: ((CheckersModel.Player) -> Unit)? = null

    init {
        checkersBoardView.checkersModel = checkersModel
        checkersBoardView.onSquareTappedListener = { row, col -> onSquareTapped(row, col) }

    }

    public fun onSquareTapped(row: Int, col: Int) {
        mustCapture = false
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val piece = checkersModel.board[r][c]
                if (piece != null && piece.player == checkersModel.currentPlayer && checkersModel.canCapture(r, c)) {
                    mustCapture = true
                    break
                }
            }
            if (mustCapture) break
        }

        if (selectedRow == null && selectedCol == null) {
            val piece = checkersModel.board[row][col]

            if (piece != null && piece.player == checkersModel.currentPlayer) {
                if (mustCapture && !checkersModel.canCapture(row, col)) {
                    // If the player must capture a piece and the selected piece cannot capture, ignore the tap
                    return
                }
                selectedRow = row
                selectedCol = col
            }
        } else {
            val moveResult = checkersModel.movePiece(selectedRow!!, selectedCol!!, row, col)
            if (moveResult) {
                // Move was successful
                val isCaptureMove = Math.abs(selectedRow!! - row) == 2
                if (isCaptureMove && checkersModel.canCapture(row, col)) {
                    // If the player can capture another piece after a capture, the jump must be made
                    mustCapture = true
                    selectedRow = row
                    selectedCol = col
                } else {
                    // If the player cannot capture another piece or it wasn't a capture move, reset selection and switch the current player
                    selectedRow = null
                    selectedCol = null
                    mustCapture = false
                    checkersModel.currentPlayer =
                        if (checkersModel.currentPlayer == CheckersModel.Player.RED) CheckersModel.Player.BLACK else CheckersModel.Player.RED
                }
                // Check if the game is over
                if (checkersModel.isGameOver()) {
                    gameOverListener?.invoke(checkersModel.currentPlayer)
                }
            } else {
                // Move was not successful, update the selection
                val piece = checkersModel.board[row][col]
                if (piece != null && piece.player == checkersModel.currentPlayer) {
                    if (mustCapture &&                 !checkersModel.canCapture(row, col)) {
                        // If the player must capture a piece and the selected piece cannot capture, ignore the tap
                        return
                    }
                    selectedRow = row
                    selectedCol = col
                } else {
                    selectedRow = null
                    selectedCol = null
                }
            }

            // Update the view to reflect the changes in the model
            checkersBoardView.invalidate()
        }
    }







    fun resetGame() {
        checkersModel.reset()
        checkersBoardView.invalidate()
    }
}

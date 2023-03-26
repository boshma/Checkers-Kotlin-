package model

class CheckersModel {
    private val boardSize = 8
    val board = Array(boardSize) { arrayOfNulls<Piece>(boardSize) }
    var currentPlayer = Player.RED
    val redPlayerScore: Int
        get() = board.flatten().count { it?.player == Player.RED }

    val blackPlayerScore: Int
        get() = board.flatten().count { it?.player == Player.BLACK }

    init {
        setupBoard()
    }

    private fun setupBoard() {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = when (row) {
                        in 0..2 -> Piece(Player.BLACK)
                        in 5..7 -> Piece(Player.RED)
                        else -> null
                    }
                }
            }
        }
    }

    fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        if (!isValidMove(fromRow, fromCol, toRow, toCol)) {
            return false
        }

        val piece = board[fromRow][fromCol]
        board[toRow][toCol] = piece
        board[fromRow][fromCol] = null

        // Check if a piece was captured
        if (Math.abs(fromRow - toRow) == 2) {
            val capturedRow = (fromRow + toRow) / 2
            val capturedCol = (fromCol + toCol) / 2
            board[capturedRow][capturedCol] = null


        }
        // Check if a piece should be promoted to king
        if (toRow == 0 && piece?.player == Player.RED || toRow == boardSize - 1 && piece?.player == Player.BLACK) {
            piece?.isKing = true
        }

        return true
    }

    // A helper function to check if a piece can capture another piece
    fun canCapture(row: Int, col: Int): Boolean {
        val piece = board[row][col]
        if (piece == null) {
            return false
        }

        val directions = if (piece.isKing) {
            arrayOf(-1, 1)
        } else {
            if (piece.player == Player.RED) {
                arrayOf(-1)
            } else {
                arrayOf(1)
            }
        }

        for (direction in directions) {
            for (captureRow in arrayOf(row - 2 * direction, row + 2 * direction)) {
                for (captureCol in arrayOf(col - 2, col + 2)) {
                    if (isValidMove(row, col, captureRow, captureCol)) {
                        return true
                    }
                }
            }
        }

        return false
    }






    fun isValidMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val piece = board[fromRow][fromCol]

        if (piece == null || piece.player != currentPlayer) {
            return false
        }

        if (toRow !in 0 until boardSize || toCol !in 0 until boardSize || board[toRow][toCol] != null) {
            return false
        }

        val rowDifference = Math.abs(fromRow - toRow)
        val colDifference = Math.abs(fromCol - toCol)
        if (rowDifference != colDifference || rowDifference !in 1..2) {
            return false
        }

        // Verify movement direction
        val direction = if (piece.player == Player.RED) -1 else 1
        if ((toRow - fromRow) * direction < 0 && !piece.isKing) {
            return false
        }

        // If it's a capture move, check if the captured piece belongs to the opponent
        if (rowDifference == 2) {
            val capturedRow = (fromRow + toRow) / 2
            val capturedCol = (fromCol + toCol) / 2
            val capturedPiece = board[capturedRow][capturedCol]

            if (capturedPiece == null || capturedPiece.player == currentPlayer) {
                return false
            }
        } else {
            // Check if there are any capture moves available
            var hasCapture = false
            for (row in 0 until boardSize) {
                for (col in 0 until boardSize) {
                    if (board[row][col]?.player == currentPlayer && canCapture(row, col)) {
                        hasCapture = true
                        break
                    }
                }
            }
            if (hasCapture) {
                return false
            }
        }

        return true
    }

    fun hasPieceBecameKing(row: Int, col: Int): Boolean {
        val piece = board[row][col]
        return (row == 0 && piece?.player == Player.RED) || (row == boardSize - 1 && piece?.player == Player.BLACK)
    }


    fun isGameOver(): Boolean {
        return redPlayerScore == 0 || blackPlayerScore == 0
    }


    fun reset() {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = when (row) {
                        in 0..2 -> Piece(Player.BLACK)
                        in 5..7 -> Piece(Player.RED)
                        else -> null
                    }
                } else {
                    board[row][col] = null
                }
            }
        }
        currentPlayer = Player.RED
    }


    data class Piece(val player: Player, var isKing: Boolean = false)

    enum class Player {
        RED, BLACK
    }
}



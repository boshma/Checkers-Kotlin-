package view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.checkersgame.R
import model.CheckersModel

class CheckersBoardView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint()
    private var squareSize: Float = 0f

    private val redPieceDrawable: Drawable =
        ContextCompat.getDrawable(context, R.drawable.red_piece)!!
    private val blackPieceDrawable: Drawable =
        ContextCompat.getDrawable(context, R.drawable.black_piece)!!
    private val redKingDrawable: Drawable =
        ContextCompat.getDrawable(context, R.drawable.red_king)!!
    private val blackKingDrawable: Drawable =
        ContextCompat.getDrawable(context, R.drawable.black_king)!!

    lateinit var checkersModel: CheckersModel
    lateinit var onSquareTappedListener: (row: Int, col: Int) -> Unit

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Math.min(measuredWidth, measuredHeight)
        squareSize = (size / 8).toFloat()
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw board
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                paint.color = if ((row + col) % 2 == 0) Color.WHITE else Color.parseColor("#D2B48C")
                canvas.drawRect(
                    col * squareSize,
                    row * squareSize,
                    (col + 1) * squareSize,
                    (row + 1) * squareSize,
                    paint
                )

                // Draw pieces
                val piece = checkersModel.board[row][col]
                if (piece != null) {
                    val drawable = when {
                        piece.player == CheckersModel.Player.RED && !piece.isKing -> redPieceDrawable
                        piece.player == CheckersModel.Player.BLACK && !piece.isKing -> blackPieceDrawable
                        piece.player == CheckersModel.Player.RED && piece.isKing -> redKingDrawable
                        piece.player == CheckersModel.Player.BLACK && piece.isKing -> blackKingDrawable
                        else -> null
                    }
                    drawable?.let {
                        it.setBounds(
                            (col * squareSize).toInt(),
                            (row * squareSize).toInt(),
                            ((col + 1) * squareSize).toInt(),
                            ((row + 1) * squareSize).toInt()
                        )
                        it.draw(canvas)
                    }
                }
            }
        }
    }



        override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val col = (event.x / squareSize).toInt()
            val row = (event.y / squareSize).toInt()

            if (col in 0..7 && row in 0..7) {
                onSquareTappedListener(row, col)
            }
            return true
        }
        return super.onTouchEvent(event)
    }
}


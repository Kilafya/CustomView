package com.drayl.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

typealias OnCellActionListener = (row: Int, column: Int, field: TicTacToeField) -> Unit

class TicTacToeView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.ticTacAttrStyle,
    defStyleRes: Int = R.style.DefaultTicTacToeStyle,
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    var actionListener: OnCellActionListener? = null

    private var player1Color = DEFAULT_COLOR_PLAYER1
    private var player2Color = DEFAULT_COLOR_PLAYER2
    private var gridColor = DEFAULT_COLOR_GRID

    private lateinit var player1Paint: Paint
    private lateinit var player2Paint: Paint
    private lateinit var gridPaint: Paint

    private var cellSize: Float = 0f
    private var cellPadding: Float = 0f
    private val fieldRect = RectF(0f, 0f, 0f, 0f)
    private val cellRect = RectF(0f, 0f, 0f, 0f)

    var ticTacToeField: TicTacToeField? = null
        set(value) {
            field?.removeFieldChangeListener(listener)
            value?.addFieldChangeListener(listener)

            updateFieldSizes()
            invalidate()
            requestLayout()

            field = value
        }

    init {
        if (attributeSet != null) {
            initAttribute(attributeSet, defStyleAttr, defStyleRes)
        }
        initPaints()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when(event.action) {
            MotionEvent.ACTION_DOWN -> true
            MotionEvent.ACTION_UP -> {
                val field = ticTacToeField ?: return false
                val row = ((event.y - fieldRect.top) / cellSize).toInt()
                val column = ((event.x - fieldRect.left) / cellSize).toInt()
                if (row in 0 until field.rows && column in 0 until field.columns) {
                    actionListener?.invoke(row, column, field)
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isThereView()) {
            drawGrid(canvas)
            drawCells(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateFieldSizes()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        val rows = ticTacToeField?.rows ?: 0
        val columns = ticTacToeField?.columns ?: 0
        val desiredWidth = max(minWidth, rows * dp(DESIRED_CELL_SIZE) + paddingLeft + paddingRight)
        val desiredHeight = max(minHeight, columns * dp(DESIRED_CELL_SIZE) + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec),
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ticTacToeField?.addFieldChangeListener(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ticTacToeField?.removeFieldChangeListener(listener)
    }



    private fun drawGrid(canvas: Canvas) {
        val field = ticTacToeField ?: return
        val startX = fieldRect.left
        val endX = fieldRect.right

        for (i in 0..field.rows) {
            val y = fieldRect.top + i * cellSize
            canvas.drawLine(startX, y, endX, y, gridPaint)
        }

        val startY = fieldRect.top
        val endY = fieldRect.bottom

        for (i in 0..field.columns) {
            val x = fieldRect.left + i * cellSize
            canvas.drawLine(x, startY, x, endY, gridPaint)
        }
    }

    private fun drawCells(canvas: Canvas) {
        val field = ticTacToeField ?: return

        for (row in 0 until field.rows) {
            val startY = fieldRect.top + row * cellSize
            for (column in 0 until field.columns) {
                val startX = fieldRect.left + column * cellSize
                when (field.getCell(row, column)) {
                    Cell.PLAYER_1 -> drawPlayer1(canvas, startX, startY)
                    Cell.PLAYER_2 -> drawPlayer2(canvas, startX, startY)
                    Cell.EMPTY -> Unit
                }
            }
        }
    }

    private fun drawPlayer1(canvas: Canvas, startX: Float, startY: Float) {
        initCellRect(startX, startY)
        canvas.drawLine(cellRect.left, cellRect.top, cellRect.right, cellRect.bottom, player1Paint)
        canvas.drawLine(cellRect.right, cellRect.top, cellRect.left, cellRect.bottom, player1Paint)
    }

    private fun drawPlayer2(canvas: Canvas, startX: Float, startY: Float) {
        initCellRect(startX, startY)
        canvas.drawCircle(
            cellRect.centerX(),
            cellRect.centerY(),
            cellSize / 2f - cellPadding,
            player2Paint
        )
    }

    private fun initCellRect(startX: Float, startY: Float) {
        cellRect.top = startY + cellPadding
        cellRect.left = startX + cellPadding
        cellRect.right = startX + cellSize - cellPadding
        cellRect.bottom = startY + cellSize - cellPadding
    }

    private fun isThereView(): Boolean {
        return ticTacToeField != null && cellSize > 0f
            && fieldRect.width() > 0f && fieldRect.height() > 0f
    }

    private fun updateFieldSizes() {
        val field = ticTacToeField ?: return
        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom
        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellHeight, cellWidth)
        cellPadding = cellSize * 0.2f

        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    private fun initPaints() {
        player1Paint = getPaint(player1Color, dp(STROKE_WIDTH_PLAYER))
        player2Paint = getPaint(player2Color, dp(STROKE_WIDTH_PLAYER))
        gridPaint = getPaint(gridColor, dp(STROKE_WIDTH_GRID))
    }

    private fun dp(num: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, num, resources.displayMetrics)
    }

    private fun dp(num: Int): Int {
        return dp(num.toFloat()).toInt()
    }

    private fun getPaint(currentColor: Int, currentStroke: Float): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = currentColor
            style = Paint.Style.STROKE
            strokeWidth = currentStroke
        }
    }

    private fun initAttribute(attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TicTacToeView, defStyleAttr, defStyleRes)

        player1Color = typedArray.getColor(R.styleable.TicTacToeView_player1Color, DEFAULT_COLOR_PLAYER1)
        player2Color = typedArray.getColor(R.styleable.TicTacToeView_player2Color, DEFAULT_COLOR_PLAYER2)
        gridColor = typedArray.getColor(R.styleable.TicTacToeView_gridColor, DEFAULT_COLOR_GRID)

        typedArray.recycle()
    }

    private val listener: OnFieldChangeListener = {
        invalidate()
    }

    companion object {
        private const val DEFAULT_COLOR_PLAYER1 = Color.RED
        private const val DEFAULT_COLOR_PLAYER2 = Color.GREEN
        private const val DEFAULT_COLOR_GRID = Color.GRAY

        private const val DESIRED_CELL_SIZE = 50
        private const val STROKE_WIDTH_PLAYER = 3f
        private const val STROKE_WIDTH_GRID = 1f
    }
}
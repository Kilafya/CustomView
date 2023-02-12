package com.drayl.customview

typealias OnFieldChangeListener = (field: TicTacToeField) -> Unit

data class TicTacToeField(
    val rows: Int,
    val columns: Int,
) {
    private val cells = Array(rows) { Array(columns) { Cell.EMPTY } }

    private val listeners = mutableSetOf<OnFieldChangeListener>()

    fun getCell(row: Int, column: Int): Cell{
        return if (isCurrentPoint(row, column)) {
            cells[row][column]
        } else {
            throw IllegalArgumentException("Going beyond borders")
        }
    }

    fun setCell(row: Int, column: Int, value: Cell) {
        if (isCurrentPoint(row, column)) {
            cells[row][column] = value
            listeners.forEach {
                it.invoke(this)
            }
        } else {
            throw IllegalArgumentException("Going beyond borders")
        }
    }

    fun addFieldChangeListener(listener: OnFieldChangeListener) {
        listeners.add(listener)
    }

    fun removeFieldChangeListener(listener: OnFieldChangeListener) {
        listeners.remove(listener)
    }

    fun removeAllListeners() {
        listeners.clear()
    }

    private fun isCurrentPoint(row: Int, column: Int): Boolean {
        return row in 0 until rows && column in 0 until columns
    }
}
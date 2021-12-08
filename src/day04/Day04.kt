package day04

import ANSI_GREEN
import ANSI_RED
import ANSI_RESET
import readInput

fun main() {
    fun List<String>.getBoardLines() = chunked(indexOf("") + 1)
    fun List<String>.getDrawns() = first().split(",").map { it.toInt() }
    fun List<String>.getBoards() = drop(2).getBoardLines()
        .mapIndexed { index, rows ->
            Board(
                rows.map { row -> Cell.fromRawData(row) }.filter { row -> row.isNotEmpty() },
                index
            )
        }

    fun List<String>.buildGame(): Game = Game(getDrawns(), getBoards())

    fun part1(input: List<String>) = input
        .buildGame()
        .playToWin()

    fun part2(input: List<String>) = input
        .buildGame()
        .playToLose()

    val testInput = readInput("day04/Day04")
    part1(testInput).apply {
        println(this)
    }
    part2(testInput).apply {
        println(this)
    }
}

data class Game(val drawn: List<Int>, val boards: List<Board>) {
    fun playToWin(): Pair<Int, Pair<Int, Board?>> =
        applyDrawn(drawn.first()).let { game ->
            game.winningBoard().let {
                when (it) {
                    null -> game.playToWin()
                    else -> drawn.first() * it.getScore() to (drawn.first() to it)
                }
            }
        }

    fun playToLose(): Pair<Int, Pair<Int, Board?>> =
        playToWin().let { (score, winningBoardData) ->
            winningBoardData.let { (_, board) ->
                copy(boards = boards.filter { it.id != board?.id })
                    .let { newGame ->
                        when (newGame.boards.size) {
                            0 -> (score to winningBoardData)
                            else -> newGame.playToLose()
                        }
                    }
            }
        }

    private fun winningBoard() = boards.firstOrNull { it.won() }
    private fun applyDrawn(currentDrawn: Int) =
        copy(boards = boards.map { it.tryMark(currentDrawn) }, drawn = drawn.drop(1))
}

data class Cell(val value: Int, val marked: Boolean) {
    fun tryMark(drawn: Int) = if (!marked && value == drawn) copy(marked = true) else this
    fun getScore() = if (marked) 0 else value

    override fun toString(): String {
        return (if (marked) ANSI_GREEN else ANSI_RED) + value.toString().padStart(3) + ANSI_RESET
    }

    companion object {
        fun fromRawData(data: String) = data.split(" ").filter { it.isNotEmpty() }.map { Cell(it.toInt(), false) }
    }
}

data class Board(val rows: List<List<Cell>>, val id: Int) {
    fun tryMark(drawn: Int) = copy(rows = rows.map { row -> row.map { cell -> cell.tryMark(drawn) } })
    fun won() = checkRows(rows union (invert(this).rows))
    fun getScore() = rows.flatMap { it.map { cell -> cell.getScore() } }.sum()

    private fun checkRows(rowsToCheck: Set<List<Cell>>) = rowsToCheck.any { it.all { cell -> cell.marked } }

    override fun toString(): String = "\n --- \n" +
            rows.joinToString("\n") { it.joinToString(" ") { cell -> cell.toString() } } +
            "\n --- "

    companion object {
        fun invert(board: Board) =
            board.copy(rows = board.rows.first().indices.map { index -> board.rows.map { it[index] } })
    }
}
package day04

import readInput

fun main() {
    fun List<String>.getBoardLines() = chunked(indexOf("") + 1)
    fun List<String>.getDrawns() = first().split(",").map { it.toInt() }
    fun List<String>.getBoards() = drop(2).getBoardLines()
        .map { rows -> Board(rows.map { row -> Cell.fromRawData(row) }.filter { row -> row.isNotEmpty() }) }

    fun List<String>.buildGame(): Game = Game(getDrawns(), getBoards())

    fun part1(input: List<String>) = input
        .buildGame()
        .play()

    fun part2(input: List<String>) = input.size

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day04/Day04")
    println(part1(testInput))
    check(part1(testInput).first == 4512)

    // val input = readInput("day04/Day04")
    // println(part1(input))
    /* println(part2(input)) */
}

data class Game(val drawn: List<Int>, val boards: List<Board>) {
    fun play(): Pair<Int, Pair<Int, Board?>> {
        applyDrawn(drawn.first()).let {
            it.winningBoard().let { winningBoard ->
                return@play if (winningBoard != null) {
                    drawn.first() * winningBoard.getScore() to (drawn.first() to it.winningBoard())
                } else it.play()
            }
        }
    }

    private fun winningBoard() = boards.firstOrNull { it.won() }
    private fun applyDrawn(currentDrawn: Int): Game {
        // println(boards)
        return copy(boards = boards.map { it.tryMark(currentDrawn) }, drawn = drawn.drop(1))
    }

}

data class Cell(val value: Int, val marked: Boolean) {
    fun tryMark(drawn: Int) = if (!marked && value == drawn) copy(marked = true) else this
    fun getScore() = if (marked) 0 else value

    override fun toString(): String {
        val ANSI_RESET = "\u001B[0m"
        val ANSI_GREEN = "\u001B[32m"
        val ANSI_RED = "\u001B[31m"
        return (if (marked) ANSI_GREEN else ANSI_RED) + value.toString().padStart(3) + ANSI_RESET
    }

    companion object {
        fun fromRawData(data: String) = data.split(" ").filter { it.isNotEmpty() }.map { Cell(it.toInt(), false) }
    }
}

data class Board(val rows: List<List<Cell>>) {
    fun tryMark(drawn: Int) = copy(rows = rows.map { row -> row.map { cell -> cell.tryMark(drawn) } })
    fun won() = checkRows(rows union (invert(this).rows))
    fun getScore() = rows.flatMap { it.map { cell -> cell.getScore() } }.sum()

    private fun checkRows(rowsToCheck: Set<List<Cell>>) = rowsToCheck.any { it.all { cell -> cell.marked } }

    override fun toString(): String = "\n --- \n" +
            rows.joinToString("\n") { it.joinToString(" ") { cell -> cell.toString() } } +
            "\n --- "

    companion object {
        fun invert(board: Board) = Board(board.rows.first().indices.map { index -> board.rows.map { it[index] } })
    }
}
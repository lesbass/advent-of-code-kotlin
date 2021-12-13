package day13

import ANSI_RED
import ANSI_RESET
import readInput

fun main() {

    fun List<String>.parseData() =
        partition { indexOf(it) < indexOf("") }
            .let { (first, second) ->
                Board.fromRawData(first) to second.drop(1).map { Command.fromRawData(it) }
            }

    fun Pair<Board, List<Command>>.executeFirstCommand() =
        first.executeCommand(second.first())

    fun part1(input: List<String>) = input.parseData().executeFirstCommand().dots.size
    fun part2(input: List<String>) = input.parseData().let { (board, commands) ->
        commands.fold(board) { acc, command ->
            acc.executeCommand(command)
        }
    }

    val testInput = readInput("day13/Day13")
    part1(testInput).apply {
        println(this)
        assert(this == 790)
    }
    part2(testInput).apply {
        println(this.print())
    }

}

data class Coordinates(val x: Int, val y: Int) {

    private fun reflectX(level: Int) = if (x > level) copy(x = 2 * level - x) else this
    private fun reflectY(level: Int) = if (y > level) copy(y = 2 * level - y) else this

    fun executeCommand(command: Command) = when (command.axis) {
        "x" -> reflectX(command.level)
        else -> reflectY(command.level)
    }
}


data class Dot(val position: Coordinates) {

    fun executeCommand(command: Command) = copy(position = position.executeCommand(command))

    companion object {
        fun fromRawData(raw: String) =
            raw.split(",")
                .take(2)
                .map { it.toInt() }
                .let { (x, y) -> Dot(Coordinates(x, y)) }
    }
}


data class Board(val dots: List<Dot>) {

    fun executeCommand(command: Command) = copy(dots = dots.map { it.executeCommand(command) }.distinct())

    fun print() =
        (0 .. dots.maxOf { it.position.y }).forEach { y ->
            (0 .. dots.maxOf { it.position.x }).forEach { x ->
                print(" " + if(dots.any { it.position == Coordinates(x, y) }) "$ANSI_RED#$ANSI_RESET" else ".")
            }
            println()
        }

    companion object {
        fun fromRawData(raw: List<String>) = Board(raw.map { Dot.fromRawData(it) })
    }
}

data class Command(val axis: String, val level: Int) {
    companion object {
        fun fromRawData(raw: String) = raw.split(" ")
            .drop(2)
            .first()
            .split("=")
            .let { (axis, levelString) -> Command(axis, levelString.toInt()) }
    }
}
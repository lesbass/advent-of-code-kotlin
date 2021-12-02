package day02

import readInput

fun main() {
    fun part1(input: List<String>): Int = input
        .map { Command.fromRawData(it) }
        .fold(Submarine.initial()) { submarine, command -> submarine.execute(command) }
        .getSignature()

    fun part2(input: List<String>): Int = input.size

    // test if implementation meets criteria from the description, like:
    // val testInput = readInput("day02/Day02")
    //check(part1(testInput) == 5)

    val input = readInput("day02/Day02")
    println(part1(input))
    // println(part2(input))
}

data class Command(val direction: Direction, val value: Int) {
    companion object {
        fun fromRawData(raw: String): Command {
            val (direction, value) = raw.split(" ")
            return Command(Direction.valueOf(direction.uppercase()), value.toInt())
        }
    }
}

data class Submarine(val x: Int, val y: Int) {

    fun execute(command: Command) =
        when (command.direction) {
            Direction.FORWARD -> moveHorizontal(command.value)
            else -> moveVertical(command.direction, command.value)
        }

    private fun moveHorizontal(value: Int) = copy(x = x + value)

    private fun moveVertical(direction: Direction, value: Int) =
        copy(y = y + if (direction == Direction.UP) value * -1 else value)

    fun getSignature() = x * y

    companion object {
        fun initial() = Submarine(0, 0)
    }
}

enum class Direction {
    FORWARD, UP, DOWN
}
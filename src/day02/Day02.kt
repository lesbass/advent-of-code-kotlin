package day02

import readInput

fun main() {
    fun parseCommands(input: List<String>): List<Command> = input
        .map { Command.fromRawData(it) }

    fun executeCommand(submarine: Submarine, command: Command): Submarine = submarine.execute(command)
    fun executeCommands(submarine: Submarine, commands: List<Command>) = commands
        .fold(submarine, ::executeCommand)
        .getSignature()

    fun part1(input: List<String>): Int = executeCommands(SimpleSubmarine.initial(), parseCommands(input))
    fun part2(input: List<String>): Int = executeCommands(ProSubmarine.initial(), parseCommands(input))

    // test if implementation meets criteria from the description, like:
    // val testInput = readInput("day02/Day02")
    //check(part1(testInput) == 5)

    val input = readInput("day02/Day02")
    println(part1(input))
    println(part2(input))
}

data class Command(val direction: Direction, val value: Int) {
    companion object {
        fun fromRawData(raw: String): Command {
            val (direction, value) = raw.split(" ")
            return Command(Direction.valueOf(direction.uppercase()), value.toInt())
        }
    }
}

abstract class Submarine(private val x: Int, private val y: Int) {

    fun execute(command: Command) =
        when (command.direction) {
            Direction.FORWARD -> handleHorizontalCommand(command.value)
            else -> handleVerticalCommand(command.direction, command.value)
        }

    abstract fun handleHorizontalCommand(value: Int): Submarine
    abstract fun handleVerticalCommand(direction: Direction, value: Int): Submarine

    fun getSignature() = x * y
}

data class SimpleSubmarine(val x: Int, val y: Int) : Submarine(x, y) {
    override fun handleHorizontalCommand(value: Int) = copy(x = x + value)
    override fun handleVerticalCommand(direction: Direction, value: Int) =
        copy(y = y + if (direction == Direction.UP) value * -1 else value)

    companion object {
        fun initial() = SimpleSubmarine(0, 0)
    }
}

data class ProSubmarine(val x: Int, val y: Int, val aim: Int) : Submarine(x, y) {
    override fun handleHorizontalCommand(value: Int) = copy(x = x + value, y = y + aim * value)
    override fun handleVerticalCommand(direction: Direction, value: Int) =
        copy(aim = aim + if (direction == Direction.UP) value * -1 else value)

    companion object {
        fun initial() = ProSubmarine(0, 0, 0)
    }
}

enum class Direction {
    FORWARD, UP, DOWN
}
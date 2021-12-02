package day02

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val commands = input.map { Command(it.split(" ")[0], it.split(" ")[1].toInt()) }
        val submarine = Submarine(0, 0)
        commands.forEach { (direction, value) ->
            if (direction == "forward") submarine.moveHorizontal(value)
            else submarine.moveVertical(direction, value)
        }
        return submarine.x * submarine.y
    }

    fun part2(input: List<String>): Int = input.size

    // test if implementation meets criteria from the description, like:
    // val testInput = readInput("day02/Day02")
    //check(part1(testInput) == 5)

    val input = readInput("day02/Day02")
    println(part1(input))
    // println(part2(input))
}

data class Command(val direction: String, val value: Int)

data class Submarine(var x: Int, var y: Int) {
    fun moveHorizontal(value: Int) {
        x += value
    }

    fun moveVertical(direction: String, value: Int) {
        y += if (direction == "up") value * -1 else value
    }
}
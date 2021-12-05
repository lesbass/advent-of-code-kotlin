package day05

import readInput
import kotlin.math.abs
import kotlin.math.max

fun main() {

    fun List<LineOfVent>.countIntersections(includeDiagonals: Boolean) =
        flatMap { it.getAllCoordinates(includeDiagonals) }
            .groupBy { it }
            .count { it.value.count() > 1 }

    fun List<String>.parseLinesOfVent() = map { LineOfVent.fromRawData(it) }

    fun part1(input: List<String>) = input
        .parseLinesOfVent()
        .countIntersections(false)

    fun part2(input: List<String>) = input
        .parseLinesOfVent()
        .countIntersections(true)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day05/Day05")
    part1(testInput).apply {
        println(this)
        check(this == 4993)
    }

    val input = readInput("day05/Day05")
    part2(input).apply {
        println(this)
        check(this == 21101)
    }
}

data class Coordinates(val x: Int, val y: Int) {

    fun add(deltaX: Int, deltaY: Int) = Coordinates(x + deltaX, y + deltaY)

    companion object {
        fun fromRawData(input: String) = input.split(",")
            .map { it.trim() }
            .let { Coordinates(it[0].toInt(), it[1].toInt()) }
    }
}

data class LineOfVent(val start: Coordinates, val end: Coordinates) {

    fun getAllCoordinates(includeDiagonals: Boolean): List<Coordinates> {
        return if (deltaX() == 0 || deltaY() == 0 || includeDiagonals) {
            (1..length()).fold(listOf(start)) { acc, _ ->
                acc + acc.last().add(stepX(), stepY())
            }
        } else listOf()
    }

    private fun length() = max(abs(deltaX()), abs(deltaY()))
    private fun deltaX() = end.x - start.x
    private fun deltaY() = end.y - start.y
    private fun stepX() = step(deltaX())
    private fun stepY() = step(deltaY())
    private fun step(it: Int) = it / length()

    companion object {
        fun fromRawData(input: String) = input.split("->").let {
            LineOfVent(Coordinates.fromRawData(it[0]), Coordinates.fromRawData(it[1]))
        }
    }
}
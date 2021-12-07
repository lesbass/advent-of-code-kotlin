package day07

import readInput
import kotlin.math.abs

fun main() {

    fun List<String>.parsePositions(): List<Int> = first().split(",").map { it.toInt() }
    fun computeFuelCostForPositionLinear(list: List<Int>, v: Int): Int = list.sumOf { abs(it - v) }
    fun computeFuelCostForPositionIncremental(list: List<Int>, v: Int): Int = list.sumOf { (0..abs(it - v)).sum() }
    fun List<Int>.computeFuelCosts(expr: (List<Int>, Int) -> Int): List<Int> =
        (minOf { it }..maxOf { it }).map { expr(this, it) }

    fun execute(input: List<String>, expr: (List<Int>, Int) -> Int):Int = input
        .parsePositions()
        .computeFuelCosts(expr)
        .minOf { it }
    fun part1(input: List<String>) = execute(input, ::computeFuelCostForPositionLinear)
    fun part2(input: List<String>) = execute(input, ::computeFuelCostForPositionIncremental)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day07/Day07")
    part1(testInput).apply {
        println(this)
        check(this == 364898)
    }

    val input = readInput("day07/Day07")
    part2(input).apply {
        println(this)
        check(this == 104149091)
    }
}

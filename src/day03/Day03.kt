package day03

import readInput
import kotlin.math.roundToInt

fun main() {

    fun extractNumberAtPosition(input: String, index: Int) = input[index].toString().toInt()

    fun getAverageValueAtPosition(input: List<String>, index: Int) =
        input.map { extractNumberAtPosition(it, index) }.average().roundToInt()

    fun getAverageValueAtPositionInverted(input: List<String>, index: Int) =
        if (getAverageValueAtPosition(input, index) == 1) 0 else 1

    fun getIntFromBinaryString(input: List<Int>): Int = input
        .joinToString("")
        .toInt(2)

    fun getGammaRate(input: List<String>) = getIntFromBinaryString(input.first().indices
        .map { index -> getAverageValueAtPosition(input, index) })
    fun getEpsilonRate(input: List<String>) = getIntFromBinaryString(input.first().indices
        .map { index -> getAverageValueAtPositionInverted(input, index) })

    fun part1(input: List<String>) = getGammaRate(input) * getEpsilonRate(input)

    fun part2(input: List<String>): Int = input.size

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day03/Day03_test")
    check(part1(testInput) == 198)

    val input = readInput("day03/Day03")
    println(part1(input))
    // println(part2(input))
}

package day03

import readInput
import kotlin.math.roundToInt

fun main() {

    fun extractNumberAtPosition(input: String, index: Int) = input[index].toString().toInt()

    fun List<String>.getAverageValueAtPosition(index: Int) =
        map { extractNumberAtPosition(it, index) }.average().roundToInt()

    fun List<String>.getAverageValueAtPositionInverted(index: Int) =
        getAverageValueAtPosition(index) xor 1

    fun List<Int>.getIntFromBinaryString() = joinToString("").toInt(2)

    fun List<String>.findOrNext(block: (List<String>) -> String): String =
        if (size == 1) first() else block(this)

    fun List<String>.findRating(prev: String = "", block: (List<String>) -> String): String =
        filter { it.startsWith(prev) }.findOrNext(block)

    fun List<String>.findOxygenGeneratorRating(prev: String = ""): String =
        findRating(prev) {
            it.findOxygenGeneratorRating(
                prev + it.getAverageValueAtPosition(prev.length)
            )
        }

    fun List<String>.findCO2ScrubberRating(prev: String = ""): String =
        findRating(prev) {
            it.findCO2ScrubberRating(
                prev + it.getAverageValueAtPositionInverted(prev.length)
            )
        }

    fun getGammaRate(input: List<String>) =
        input.first().indices.map { input.getAverageValueAtPosition(it) }.getIntFromBinaryString()

    fun getEpsilonRate(input: List<String>) =
        input.first().indices.map { input.getAverageValueAtPositionInverted(it) }.getIntFromBinaryString()

    fun getOxygenGeneratorRating(input: List<String>) = input.findOxygenGeneratorRating().toInt(2)

    fun getCO2ScrubberRating(input: List<String>) = input.findCO2ScrubberRating().toInt(2)

    fun part1(input: List<String>) = getGammaRate(input) * getEpsilonRate(input)

    fun part2(input: List<String>) = getOxygenGeneratorRating(input) * getCO2ScrubberRating(input)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day03/Day03_test")
    check(part2(testInput) == 230)

    val input = readInput("day03/Day03")
    println(part1(input))
    println(part2(input))
}

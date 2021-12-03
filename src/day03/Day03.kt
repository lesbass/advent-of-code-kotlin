package day03

import readInput
import kotlin.math.roundToInt

fun main() {

    fun extractNumberAtPosition(input: String, index: Int) = input[index].toString().toInt()

    fun getAverageValueAtPosition(input: List<String>, index: Int) =
        input.map { extractNumberAtPosition(it, index) }.average().roundToInt()

    fun getAverageValueAtPositionInverted(input: List<String>, index: Int) =
        if (getAverageValueAtPosition(input, index) == 1) 0 else 1

    fun getIntFromBinaryString(input: List<Int>) = input.joinToString("").toInt(2)

    fun findOxygenGeneratorRating(input: List<String>, prev: String = ""): String =
        input.filter { it.startsWith(prev) }.let {
            return@findOxygenGeneratorRating if (it.size == 1) it.first()
            else findOxygenGeneratorRating(
                input, prev + getAverageValueAtPosition(
                    it, prev.length
                )
            )
        }

    fun findCO2ScrubberRating(input: List<String>, prev: String = ""): String =
        input.filter { it.startsWith(prev) }.let {
            return@findCO2ScrubberRating if (it.size == 1) it.first()
            else findCO2ScrubberRating(
                input, prev + getAverageValueAtPositionInverted(
                    it, prev.length
                )
            )
        }

    fun getGammaRate(input: List<String>) =
        getIntFromBinaryString(input.first().indices.map { index -> getAverageValueAtPosition(input, index) })

    fun getEpsilonRate(input: List<String>) =
        getIntFromBinaryString(input.first().indices.map { index -> getAverageValueAtPositionInverted(input, index) })

    fun getOxygenGeneratorRating(input: List<String>) = findOxygenGeneratorRating(input).toInt(2)

    fun getCO2ScrubberRating(input: List<String>) = findCO2ScrubberRating(input).toInt(2)

    fun part1(input: List<String>) = getGammaRate(input) * getEpsilonRate(input)

    fun part2(input: List<String>) = getOxygenGeneratorRating(input) * getCO2ScrubberRating(input)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day03/Day03_test")
    check(part2(testInput) == 230)

    val input = readInput("day03/Day03")
    println(part1(input))
    println(part2(input))
}

package day08

import readInput

val workingDisplay = mapOf(
    "abcefg" to 0,
    "cf" to 1,
    "acdeg" to 2,
    "acdfg" to 3,
    "bcdf" to 4,
    "abdfg" to 5,
    "abdefg" to 6,
    "acf" to 7,
    "abcdefg" to 8,
    "abcdfg" to 9
)

fun main() {

    fun String.sortString() = toList().sorted().joinToString("")
    fun String.getDataFromRaw() = trim().split(" ").map { it.sortString() }
    fun List<String>.parseData() = map { it.split("|") }
        .map { Display(it[0].getDataFromRaw(), it[1].getDataFromRaw()) }

    fun String.overlapsWith(str: String) = all { str.contains(it) }
    fun String.deltaFrom(str: String) = count { !str.contains(it) }
    fun Map<String, Int>.findByValue(value: Int) = filterValues { it == value }.keys.first()
    fun Map<String, Int>.findByLength(length: Int) = filterKeys { it.length == length }.values.first()

    fun Int.isUniqueSegmentsLength() = workingDisplay.keys
        .groupBy { it.length }
        .filter { it.value.size == 1 }
        .map { it.key }
        .contains(this)

    fun guessNumber(wipDictionary: Map<String, Int>, currentSegment: String) =
        currentSegment.length.let { segmentLength ->
            wipDictionary + (currentSegment to when {
                segmentLength.isUniqueSegmentsLength() -> workingDisplay.findByLength(segmentLength)
                segmentLength == 6 -> when {
                    wipDictionary.findByValue(4).overlapsWith(currentSegment) -> 9
                    wipDictionary.findByValue(7).overlapsWith(currentSegment) -> 0
                    else -> 6
                }
                segmentLength == 5 -> when {
                    wipDictionary.findByValue(7).overlapsWith(currentSegment) -> 3
                    wipDictionary.findByValue(9).deltaFrom(currentSegment) == 1 -> 5
                    else -> 2
                }
                else -> -1
            })
        }

    fun List<String>.getTranslationDictionary() =
        sortedWith(compareBy<String> { !it.length.isUniqueSegmentsLength() }
            .thenByDescending { it.length }
        ).fold(mapOf(), ::guessNumber)

    fun Map<String, Int>.translateDataToNumber(data: List<String>) = data.map { this[it] }
        .joinToString("").toInt()

    fun Display.getData() = testData
        .getTranslationDictionary()
        .translateDataToNumber(actualData)

    fun part1(input: List<String>) = input
        .parseData()
        .flatMap { it.actualData }
        .count { it.length.isUniqueSegmentsLength() }

    fun part2(input: List<String>) = input
        .parseData()
        .sumOf { it.getData() }

    val testInput = readInput("day08/Day08")
    part1(testInput).apply {
        println(this)
    }
    part2(testInput).apply {
        println(this)
    }
}

data class Display(val testData: List<String>, val actualData: List<String>)

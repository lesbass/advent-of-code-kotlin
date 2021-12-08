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

    fun String.getDataFromRaw() = trim().split(" ")
    fun List<String>.parseData() = map { it.split("|") }
        .map { Display(it[0].getDataFromRaw(), it[1].getDataFromRaw()) }

    fun String.overlapsWith(str: String) =
        all { c -> str.contains(c) }

    fun String.deltaFrom(str: String) =
        count { c -> !str.contains(c) }

    fun guessNumber(acc: Map<Int, String>, item: String) = acc + (when (item.length) {
        6 -> (when {
            acc[4]!!.overlapsWith(item) -> 9
            acc[7]!!.overlapsWith(item) -> 0
            else -> 6
        })
        5 -> (when {
            acc[7]!!.overlapsWith(item) -> 3
            acc[9]!!.deltaFrom(item) == 1 -> 5
            else -> 2
        })
        else -> -1
    } to item)

    fun String.sortString() = toList().sortedBy { it }.joinToString("")

    fun findUniqueSegmentsLength() =
        workingDisplay.keys.groupBy { it.length }.filter { it.value.size == 1 }.map { it.key }

    fun List<String>.getCertainNumbers() = filter { findUniqueSegmentsLength().contains(it.length) }
        .fold(mapOf<Int, String>()) { acc, x ->
            acc + mapOf(workingDisplay.filter { it.key.length == x.length }.values.first() to x)
        }

    fun List<String>.getUncertainNumbers() = filter { !findUniqueSegmentsLength().contains(it.length) }
        .sortedByDescending { it.length }

    fun List<String>.getTranslationDictionary() = getUncertainNumbers()
        .fold(getCertainNumbers(), ::guessNumber)
        .map { (number, segment) -> segment.sortString() to number }
        .toMap()

    fun Map<String, Int>.mapDictionaryToData(data: List<String>) = data.map { this[it.sortString()] }

    fun Display.getData() = testData
        .getTranslationDictionary()
        .mapDictionaryToData(actualData)
        .joinToString("").toInt()

    fun part1(input: List<String>) = input
        .parseData()
        .flatMap { it.actualData }
        .filter { findUniqueSegmentsLength().contains(it.length) }
        .size

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

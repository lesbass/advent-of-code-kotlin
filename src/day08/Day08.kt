package day08

import readInput

fun main() {

    fun String.getDataFromRaw() = trim().split(" ")
    fun List<String>.parseData() = map { it.split("|") }
        .map { Display(it[0].getDataFromRaw(), it[1].getDataFromRaw()) }

    fun part1(input: List<String>) = input
        .parseData()
        .flatMap { it.actualData }
        .filter { listOf(2, 3, 4, 7).contains(it.length) }
        .size

    fun part2(input: List<String>) = input
        .parseData().sumOf {
            it.getData()
        }

    val testInput = readInput("day08/Day08")
    part1(testInput).apply {
        println(this)
    }
    part2(testInput).apply {
        println(this)
    }
}

fun String.overlapsWith(str: String) =
    all { c -> str.contains(c) }

fun String.deltaFrom(str: String) =
    count { c -> !str.contains(c) }

data class Display(val testData: List<String>, val actualData: List<String>) {

    private fun findUniqueSegmentLength() =
        workingDisplay.keys.groupBy { it.length }.filter { it.value.size == 1 }.map { it.key }

    fun getData() =
        getTranslationDictionary().let { dict ->
            actualData
                .map { d ->
                    dict[sortString(d)]
                }
        }.joinToString("").toInt()

    private fun getCertainNumbers() = testData
        .filter { findUniqueSegmentLength().contains(it.length) }
        .fold(mapOf<Int, String>()) { acc, x ->
            acc + mapOf(workingDisplay.filter { it.key.length == x.length }.values.first() to x)
        }

    private fun getUncertainNumbers() = testData
        .filter { !findUniqueSegmentLength().contains(it.length) }
        .sortedByDescending { it.length }

    private fun guessNumber(acc: Map<Int, String>, item: String) = acc + (when (item.length) {
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

    private fun getTranslationDictionary() =
        getUncertainNumbers()
            .fold(getCertainNumbers(), ::guessNumber)
            .map { v -> (sortString(v.value) to v.key) }
            .toMap()

    private fun sortString(str: String) = str.toList().sortedBy { it }.joinToString("")

    private val workingDisplay = mapOf(
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
}

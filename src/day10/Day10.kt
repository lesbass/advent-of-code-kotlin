package day10

import readInput

val syntax = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)

val corruptionScores = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137,
)

val autocompleteScores = mapOf(
    '(' to 1,
    '[' to 2,
    '{' to 3,
    '<' to 4,
)

fun main() {

    fun processCharOnChecker(checker: Checker, char: Char) =
        if (!checker.valid) checker else checker.addOrRemove(char)

    fun String.checkRow() = asIterable().fold(Checker(this), ::processCharOnChecker)

    fun List<String>.getCorruptedLines() = map { it.checkRow() }.filter { !it.valid }
    fun List<String>.getIncompleteLines() = map { it.checkRow() }.filter { it.valid }
    fun List<Long>.getMiddleValue() = sorted()
        .let {
            it[(it.size - (it.size % 2)) / 2]
        }

    fun part1(input: List<String>) = input.getCorruptedLines().sumOf { it.corruptionScore }

    fun part2(input: List<String>) = input.getIncompleteLines()
        .map { it.getAutocompleteScore() }
        .getMiddleValue()

    val testInput = readInput("day10/Day10")
    part1(testInput).apply {
        println(this)
    }
    part2(testInput).apply {
        println(this)
    }
}


data class Checker(
    val originalRow: String,
    val elements: List<Char> = listOf(),
    val processedRow: String = "",
    val valid: Boolean = true,
    val corruptionScore: Int = 0
) {
    fun addOrRemove(char: Char) =
        when {
            char.isOpeningChar() -> append(char)
            isExpectedClosingChar(char) -> remove(char)
            else -> copy(valid = false, corruptionScore = corruptionScores[char]!!)
        }

    fun getAutocompleteScore() = elements.reversed().fold(0.toLong(), ::computeAutocompleteScore)

    private fun computeAutocompleteScore(score: Long, char: Char) = score * 5 + autocompleteScores[char]!!

    private fun expectedChar(char: Char): Char = syntax.filterValues { it == char }.keys.first()

    private fun isExpectedClosingChar(char: Char) =
        when {
            !char.isClosingChar() -> false
            elements.isEmpty() -> false
            elements.last() == expectedChar(char) -> true
            else -> false
        }

    private fun append(char: Char) = copy(elements = elements + char, processedRow = processedRow + char)
    private fun remove(char: Char) = copy(elements = elements.dropLast(1), processedRow = processedRow + char)

    private fun Char.isOpeningChar() = syntax.keys.contains(this)
    private fun Char.isClosingChar() = syntax.values.contains(this)
}
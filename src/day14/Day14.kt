package day14

import readInput

fun main() {

    fun List<String>.parseCommands() =
        drop(1).associate {
            it.split(" -> ")
                .let { (pair, elementToAdd) -> pair to elementToAdd }
        }

    fun List<String>.parseData() =
        partition { indexOf(it) < indexOf("") }
            .let { (rawPolymer, rawCommands) ->
                rawPolymer.first() to rawCommands.parseCommands()
            }

    fun String.growPolymer(dictionary: CommandDict) =
        (first() + dictionary[this].toString() + drop(1).first()).windowed(2)

    fun List<Pair<String, Long>>.reduceToPolymerMap() = groupBy { (polymer) -> polymer }
        .map { (polymer, polymerData) -> polymer to polymerData.sumOf { (_, occurrences) -> occurrences } }
        .toMap()

    fun Pair<PolymerMap, CommandDict>.executeSingle() =
        let { (polymer, dictionary) ->
            polymer.asIterable()
                .fold(mapOf<String, Long>()) { polymerMap, (prevPolymer, prevOccurrences) ->
                    (polymerMap.toList() + prevPolymer.growPolymer(dictionary)
                        .map { polymer -> polymer to prevOccurrences })
                        .reduceToPolymerMap()
                }
        }

    fun Pair<PolymerMap, CommandDict>.executeTimes(times: Int) =
        let { (polymerMap, commandDict) ->
            (1..times).fold(polymerMap) { prevPolymerMap, _ ->
                (prevPolymerMap to commandDict).executeSingle()
            }
        }

    fun String.getEdgeChars() = listOf(first(), last())
    fun List<Pair<Char, Long>>.getLeastFrequentChar() = maxByOrNull { (_, occurrences) -> occurrences }!!.first
    fun List<Pair<Char, Long>>.checkIfMinIsInternal(originalPolymer: String) =
        if (!originalPolymer.getEdgeChars().contains(getLeastFrequentChar())
        ) 1 else 0

    fun List<Pair<Char, Long>>.getMin(originalPolymer: String) =
        minOf { (_, occurrences) -> occurrences } / 2 + checkIfMinIsInternal(originalPolymer)

    fun List<Pair<Char, Long>>.getMax() = maxOf { (_, occurrences) -> occurrences } / 2
    fun List<Pair<Char, Long>>.getResult(originalPolymer: String) = getMax() - getMin(originalPolymer)
    fun PolymerMap.groupAndCountByChar() =
        flatMap { (couple, occurrences) -> couple.asIterable().map { char -> char to occurrences } }
            .groupBy { (char) -> char }
            .map { (char, charData) -> char to charData.sumOf { (_, occurrences) -> occurrences } }

    fun PolymerMap.getResult(originalPolymer: String) =
        groupAndCountByChar().getResult(originalPolymer)

    fun List<String>.execute(n: Int) = parseData()
        .let { (polymer, dictionary) ->
            (polymer.windowed(2).groupBy { it }
                .map { (couple, occurrences) -> couple to occurrences.size.toLong() }.toMap() to dictionary)
                .executeTimes(n).getResult(polymer)
        }

    fun part1(input: List<String>) = input.execute(10)
    fun part2(input: List<String>) = input.execute(40)

    val testInput = readInput("day14/Day14")
    part1(testInput).apply {
        println(this)
        assert(this == 2194.toLong())
    }
    part2(testInput).apply {
        println(this)
        assert(this == 2360298895777)
    }
}

typealias CommandDict = Map<String, String>
typealias PolymerMap = Map<String, Long>
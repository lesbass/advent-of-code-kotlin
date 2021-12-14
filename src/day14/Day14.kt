package day14

import readInput

fun main() {

    fun List<String>.parseData() =
        partition { indexOf(it) < indexOf("") }
            .let { (first, second) ->
                first.first() to (second.drop(1).map {
                    it.split(" -> ")
                        .let { (pair, elementToAdd) -> pair to elementToAdd }
                }).toMap()
            }

    fun Pair<String, Map<String, String>>.executeSingle() =
        let { (polymer, dictionary) ->
            polymer.windowed(2).foldIndexed("") { index, acc, item ->
                acc + (if (index == 0) item.first() else "") + dictionary[item] + item.drop(1).first()
            }
        }

    fun Pair<String, Map<String, String>>.execute(times: Int) =
        (1..times).fold(first) { acc, _ ->
            (acc to second).executeSingle()
        }

    fun String.getResult() = asIterable().groupBy { it }.let {
        it.maxOf { x -> x.value.size } - it.minOf { x -> x.value.size }
    }

    fun part1(input: List<String>) = input.parseData().execute(10).getResult()
    fun part2(input: List<String>) = input.parseData().execute(40).getResult()

    val testInput = readInput("day14/Day14_test")
    //part1(testInput).apply {
    //    println(this)
        //assert(this == 790)
    //}
    part2(testInput).apply {
        println(this)
    }

}
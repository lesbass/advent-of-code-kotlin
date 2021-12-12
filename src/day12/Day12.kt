package day12

import readInput

typealias Segment = Pair<Cavern, Cavern>
typealias Path = List<Cavern>


fun main() {

    fun List<String>.parseData(): List<Segment> = map {
        it.split("-").let { (first, second) ->
            Cavern.fromRawData(first) to Cavern.fromRawData(second)
        }
    }

    fun filterOutUsedSmallCaverns(currentPath: Path, n: Int): List<Cavern> =
        currentPath.filter { it.isSmall }.let { smallCaverns ->
            when {
                smallCaverns.groupBy { it }.any { it.value.size > n } -> smallCaverns.distinct()
                else -> listOf()
            }
        }

    fun List<Segment>.computePaths(start: Cavern, allowedSmallSegments: Int, currentPath: Path = listOf()): List<Path> =
        filter { (first) -> first == start }
            .map {
                (currentPath + it.second).let { newPath ->
                    when {
                        it.second.isEnd -> listOf(newPath)
                        it.second.isSmall -> filter { s ->
                            !filterOutUsedSmallCaverns(newPath, allowedSmallSegments).contains(s.second)
                        }.computePaths(it.second, allowedSmallSegments, newPath)
                        else -> computePaths(it.second, allowedSmallSegments, newPath)
                    }
                }
            }.flatten()

    fun Pair<Cavern, Cavern>.getReturnTrip() = second to first

    fun List<Segment>.addReturnTrips() = flatMap {
        when {
            it.first.isStart || it.second.isEnd -> listOf(it)
            it.first.isEnd || it.second.isStart -> listOf(it.getReturnTrip())
            else -> listOf(it, it.getReturnTrip())
        }
    }

    fun List<Segment>.findStart() = map { it.first }.first { it.isStart }

    fun part1(input: List<String>) = input.parseData().addReturnTrips()
        .let { it.computePaths(it.findStart(), 0) }.size

    fun part2(input: List<String>) = input.parseData().addReturnTrips()
        .let { it.computePaths(it.findStart(), 1) }.size

    val testInput = readInput("day12/Day12")
    part1(testInput).apply {
        println(this)
        assert(this == 3856)
    }
    part2(testInput).apply {
        println(this)
        assert(this == 116692)
    }
}

data class Cavern(val name: String, val isSmall: Boolean, val isStart: Boolean = false, val isEnd: Boolean = false) {
    companion object {
        fun fromRawData(data: String) =
            Cavern(data, data.first().isLowerCase(), data == "start", data == "end")
    }
}
package day12

import readInput

typealias Segment = Pair<Cavern, Cavern>
typealias Path = List<Cavern>

fun main() {

    fun List<String>.parseData() = map {
        it.split("-").let { (first, second) ->
            Cavern.fromRawData(first) to Cavern.fromRawData(second)
        }
    }

    fun filterOutUsedSmallCaverns(currentPath: Path, n: Int) =
        currentPath.filter { it.size == CavernSize.SMALL }.let { smallCaverns ->
            when {
                smallCaverns.groupBy { it }.any { it.value.size > n } -> smallCaverns.distinct()
                else -> listOf()
            }
        }

    fun List<Segment>.computePaths(start: Cavern, allowedSmallSegments: Int, currentPath: Path = listOf()): List<Path> =
        filter { (first) -> first == start }
            .flatMap { (_, currentCavern) ->
                (currentPath + currentCavern).let { newPath ->
                    when {
                        currentCavern.position == CavernPosition.END -> listOf(newPath)
                        currentCavern.size == CavernSize.SMALL -> filter { (_, end) ->
                            !filterOutUsedSmallCaverns(newPath, allowedSmallSegments).contains(end)
                        }.computePaths(currentCavern, allowedSmallSegments, newPath)
                        else -> computePaths(currentCavern, allowedSmallSegments, newPath)
                    }
                }
            }

    fun Pair<Cavern, Cavern>.getReturnTrip() = second to first

    fun List<Segment>.addReturnTrips() = flatMap {
        when {
            it.first.position == CavernPosition.START || it.second.position == CavernPosition.END -> listOf(it)
            it.first.position == CavernPosition.END || it.second.position == CavernPosition.START -> listOf(it.getReturnTrip())
            else -> listOf(it, it.getReturnTrip())
        }
    }

    fun List<Segment>.findStart() = map { it.first }.first { it.position == CavernPosition.START }

    fun execute(input: List<String>, allowedSmallSegments: Int) = input.parseData().addReturnTrips()
        .let { it.computePaths(it.findStart(), allowedSmallSegments) }.size

    fun part1(input: List<String>) = execute(input, 0)
    fun part2(input: List<String>) = execute(input, 1)

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

data class Cavern(val name: String, val size: CavernSize, val position: CavernPosition) {
    companion object {
        fun fromRawData(data: String) =
            Cavern(data, getCavernSize(data), getCavernPosition(data))

        private fun getCavernSize(data: String) = when {
            data.first().isLowerCase() -> CavernSize.SMALL
            else -> CavernSize.BIG
        }

        private fun getCavernPosition(data: String) = when (data) {
            "start" -> CavernPosition.START
            "end" -> CavernPosition.END
            else -> CavernPosition.REGULAR
        }
    }
}

enum class CavernSize {
    BIG,
    SMALL
}

enum class CavernPosition {
    START,
    END,
    REGULAR
}
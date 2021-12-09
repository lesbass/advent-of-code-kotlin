package day09

import readInput

fun main() {

    fun List<String>.parseData() = mapIndexed { rowId, row ->
        row.asIterable().mapIndexed { colId, p -> Point(Coordinates(colId, rowId), p.toString().toInt()) }
    }

    fun Board.invert() =
        first().indices.map { index -> map { it[index] } }

    fun Row.markLowest(position: Coordinates) =
        map { if (it.position == position) it.copy(lowest = true) else it }

    fun Row.checkLowest() = let { points ->
        foldIndexed(points) { index, acc, (position, height, lowest) ->
            if (!lowest && when (index) {
                    0 -> height < points[1].height
                    size - 1 -> height < points[index - 1].height
                    else -> height < points[index - 1].height && height < points[index + 1].height
                }
            ) acc.markLowest(position) else acc
        }
    }

    fun Board.getLowestPointsOnRows() = flatMap { it.checkLowest() }.filter { it.lowest }
    fun Board.getGlobalLowestPoints() = getLowestPointsOnRows().intersect(
        invert().getLowestPointsOnRows().toSet()
    )

    fun Board.getBiggestBasins(n: Int) =
        getGlobalLowestPoints()
            .map { it.getBasin(this) }
            .sortedByDescending { it.size }
            .take(n)

    fun part1(input: List<String>) = input
        .parseData()
        .getGlobalLowestPoints()
        .sumOf { it.height + 1 }

    fun part2(input: List<String>) = input
        .parseData()
        .getBiggestBasins(3)
        .fold(1) { acc, item -> acc * item.size }

    val testInput = readInput("day09/Day09")
    part1(testInput).apply {
        println(this)
    }
    part2(testInput).apply {
        println(this)
    }
}

data class Point(val position: Coordinates, val height: Int, val lowest: Boolean = false) {
    fun getBasin(fullData: Board): List<Point> {
        val usedPoints = mutableListOf<Point>()
        fun Point.getBasinRecursive(fullData: Board): List<Point> =
            getSurroundingPoints(fullData, usedPoints)
                .let {
                    usedPoints += it
                    it
                }.flatMap {
                    if (it.height == 9) listOf() else it.getBasinRecursive(fullData) + it
                }

        return getBasinRecursive(fullData)
    }

    private fun getSurroundingPoints(fullData: Board, avoid: List<Point>) =
        position.getSurrounding(fullData.first().size, fullData.size).let { positions ->
            fullData.flatMap { row -> row.filter { positions.contains(it.position) } }
        }.filter { !avoid.contains(it) }
}

data class Coordinates(val x: Int, val y: Int) {
    fun getSurrounding(width: Int, height: Int) = listOf(
        Coordinates(x - 1, y),
        Coordinates(x + 1, y),
        Coordinates(x, y - 1),
        Coordinates(x, y + 1),
    ).filter { x > -1 && x < width && y > -1 && y < height }
}

typealias Row = List<Point>
typealias Board = List<Row>
package day09

import readInput

fun main() {

    fun List<String>.parseData() = mapIndexed { rowId, row ->
        row.asIterable().mapIndexed { id, p -> Point(rowId * 100 + id, p.toString().toInt()) }
    }

    fun List<List<Point>>.invert() =
        first().indices.map { index -> map { it[index] } }

    fun List<Point>.markLowest(point: Point) = map { if (it.id == point.id) it.copy(lowest = true) else it }

    fun List<Point>.checkLowest() = foldIndexed(this) { index, acc, point ->
        fun isFirst() = index == 0
        fun isLast() = index == size - 1

        if (
            when {
                isFirst() -> point.height < this[1].height
                isLast() -> point.height < this[index - 1].height
                else -> point.height < this[index - 1].height && point.height < this[index + 1].height
            }
        ) acc.markLowest(point) else acc
    }

    fun List<List<Point>>.getLowestPoints() = flatMap { it.checkLowest() }.filter { it.lowest }

    fun part1(input: List<String>) = input
        .parseData()
        .let { data ->
            data.getLowestPoints().filter { p -> data.invert().getLowestPoints().map { it.id }.contains(p.id) }
        }.sumOf { it.height + 1 }

    val testInput = readInput("day09/Day09")
    part1(testInput).apply {
        println(this)
    }
}

data class Point(val id: Int, val height: Int, val lowest: Boolean = false)
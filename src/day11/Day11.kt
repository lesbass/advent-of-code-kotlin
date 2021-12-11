package day11

import readInput

fun main() {

    fun List<String>.parseData() = mapIndexed { rowId, row ->
        row.asIterable().mapIndexed { colId, p -> Octopus(Coordinates(colId, rowId), p.toString().toInt()) }
    }.flatten()

    fun Board.flashAndCount(flashes: Int = 0): Pair<Board, Int> =
        firstOrNull { it.willFlash() }?.flashMe(this)?.let { (newBoard, flash) ->
            return newBoard.flashAndCount(flashes + flash)
        } ?: (this to flashes)

    fun Board.applyStep(flashes: Int = 0): Pair<Board, Int> =
        map { it.raiseLevel() }.flashAndCount(flashes)

    fun Board.isAllFlashing() = all { it.energyLevel == 0 }

    fun Board.applySteps(n: Int) =
        (1..n).fold(this to 0) { (board, flashes), _ ->
            board.applyStep(flashes)
        }

    fun Board.findAllFlashing(steps: Int = 0) : Int =
        if(!isAllFlashing()) applyStep().first.findAllFlashing(steps + 1)
        else steps

    fun part1(input: List<String>) = input.parseData().applySteps(100).second
    fun part2(input: List<String>) = input.parseData().findAllFlashing()

    val testInput = readInput("day11/Day11")
    part1(testInput).apply {
        println(this)
        assert(this == 1683)
    }
    part2(testInput).apply {
        println(this)
        assert(this == 788)
    }
}

data class Octopus(val position: Coordinates, val energyLevel: Int) {
    fun willFlash() = energyLevel > 9
    fun flashMe(fullData: Board) = if (willFlash())
        raiseSurrounding(fullData).replace(copy(energyLevel = 0)) to 1
    else fullData to 0

    fun raiseLevel(onlyIfNotFlashed: Boolean = false) =
        if (onlyIfNotFlashed && energyLevel == 0) this
        else copy(energyLevel = energyLevel + 1)

    private fun raiseSurrounding(fullData: Board) = getSurrounding(fullData).fold(fullData) { acc, item ->
        acc.replace(item.raiseLevel(true))
    }

    private fun getSurrounding(fullData: Board) =
        position.getSurrounding(fullData.maxOf { it.position.x + 1 }, fullData.maxOf { it.position.y + 1 })
            .let { positions ->
                fullData.filter { positions.contains(it.position) }
            }

    private fun Board.replace(octopus: Octopus) =
        map {
            if (it.position == octopus.position) octopus else it
        }
}

data class Coordinates(val x: Int, val y: Int) {
    fun getSurrounding(width: Int, height: Int) = listOf(
        Coordinates(x - 1, y + 1),
        Coordinates(x - 1, y),
        Coordinates(x - 1, y - 1),
        Coordinates(x, y - 1),
        Coordinates(x, y + 1),
        Coordinates(x + 1, y + 1),
        Coordinates(x + 1, y),
        Coordinates(x + 1, y - 1),
    ).filter { x > -1 && x < width && y > -1 && y < height }
}

typealias Board = List<Octopus>
package day11

import ANSI_GREEN
import ANSI_RESET
import readInput

fun Board.replace(octopus: Octopus) =
    map {
        if (it.position == octopus.position) octopus else it
    }

fun main() {

    fun List<String>.parseData() = mapIndexed { rowId, row ->
        row.asIterable().mapIndexed { colId, p -> Octopus(Coordinates(colId, rowId), p.toString().toInt()) }
    }.flatten()

    fun List<Pair<Octopus, Boolean>>.parseStepResult() =
        map { octopus -> octopus.first } to count { octopus -> octopus.second }


    fun Board.getCurrentElement(octopus: Octopus) =
        first { it.position == octopus.position }

    fun Board.applyStep(): Pair<Board, Int> =
        map { it.raiseLevel() }
            .let { board ->
                var b = board
                var flashes = 0
                while (b.any { it.willFlash() }) {
                    b.first { it.willFlash() }.flashMe(b).let { (newBoard, flash) ->
                        b = newBoard
                        flashes += flash
                    }
                }
                b to flashes
            }
    //.map { it.tryFlash() }
    //.parseStepResult()

    fun Board.applySteps(n: Int) =
        (1..n).fold(this to 0) { (board, flashes), step ->
            board.applyStep().let { x ->
                (0..x.first.maxOf { c -> c.position.y }).forEach { pY ->
                    (0..x.first.maxOf { c -> c.position.x }).forEach { pX ->
                        fun Int.printEnergyLevel() = toString().padStart(3).let {
                            if (this > 0) it
                            else ANSI_GREEN + it + ANSI_RESET
                        }
                        print(
                            x.first.first { c -> c.position == Coordinates(pX, pY) }.energyLevel.printEnergyLevel()
                        )
                    }
                    println()
                }
                println("step $step: flashes: $flashes")
                x.first to x.second + flashes
            }
        }

    fun part1(input: List<String>) = input.parseData().applySteps(3).second
    fun part2(input: List<String>) = input.size


    val testInput = readInput("day11/Day11_test")
    part1(testInput).apply {
        println(this)
    }
//    part2(testInput).apply {
//        println(this)
//    }
}

data class Octopus(val position: Coordinates, val energyLevel: Int) {
    fun willFlash() = energyLevel > 9
    fun flashMe(fullData: Board) = if (willFlash())
        getSurrounding(fullData).fold(fullData) { acc, item ->
            acc.replace(item.raiseLevel(true))
        }.replace(copy(energyLevel = 0)) to 1
    else fullData to 0

    //fun tryFlash() = flashMe() to willFlash()

    fun raiseLevel(onlyIfNotFlashed: Boolean = false) =
        if (onlyIfNotFlashed && energyLevel == 0) this
        else copy(energyLevel = energyLevel + 1)

    /*fun addSurroundingFlashes(fullData: Board) =
        getSurrounding(fullData)
            .count { it.willFlash() }
            .let {
                if (it > 0) copy(energyLevel = energyLevel + it) else this
            }*/

    private fun getSurrounding(fullData: Board) =
        position.getSurrounding(fullData.maxOf { it.position.x }, fullData.maxOf { it.position.y })
            .let { positions ->
                fullData.filter { positions.contains(it.position) }
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
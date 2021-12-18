package day15

import ANSI_GREEN
import ANSI_RESET
import readInput

fun main() {

    fun List<String>.parseData() = mapIndexed { rowId, row ->
        row.asIterable().mapIndexed { colId, p -> Position(Coordinates(colId, rowId), p.toString().toInt()) }
    }.flatten()

    fun List<Position>.startingPoint() = first { it.coordinates == Coordinates(0, 0) }
    fun List<Position>.endingPoint() =
        first { it.coordinates == Coordinates(maxOf { p -> p.coordinates.x }, maxOf { p -> p.coordinates.y }) }

    /*fun Position.nextSteps(board: List<Position>, pathTillHere: List<Position>): List<Position> =
        getSurroundingPoints(board, pathTillHere)
            .sortedBy { it.riskLevel }

    fun List<Position>.findPath(pathTillHere: List<Position>): List<List<Position>> =
        pathTillHere.last().let {
            when (it) {
                endingPoint() -> listOf(pathTillHere)
                else ->
                    it.nextSteps(this, pathTillHere).flatMap { p ->
                        findPath(pathTillHere + p)
                    }
            }
        }*/


    fun Position.nextSteps(availablePositions: List<Position>): List<Position> =
        getSurroundingPoints(availablePositions)
            .sortedWith(compareBy<Position> { it.riskLevel }.thenByDescending { it.coordinates.y })

    fun List<Position>.removePosition(position: Position) = filter { it != position }

    fun Path.getScore() = drop(1).sumOf { it.riskLevel }

    fun printBoard(path: Path, availablePositions: List<Position>) {
        val fullBoard = path + availablePositions
        val endingPosition = fullBoard.endingPoint()
        println("-- START BOARD -- PATH SCORE: ${path.getScore()}")
        (0..endingPosition.coordinates.y).forEach { y ->
            (0..endingPosition.coordinates.x).forEach { x ->
                Coordinates(x, y).let { c ->
                    if (path.map { it.coordinates }.contains(c))
                        print(ANSI_GREEN + fullBoard.first { it.coordinates == c }.riskLevel + ANSI_RESET)
                    else
                        print(fullBoard.first { it.coordinates == c }.riskLevel)
                }
            }
            println()
        }
        println("-- END BOARD --")
    }


    val testedPaths = mutableListOf<Path>()
    fun Path.sorted() = sortedBy { it.coordinates.toString() }
    fun Path.isAlreadyTested() = testedPaths.contains(this.sorted())

    fun Path.findNext(availablePositions: List<Position>): List<Path> =
        (last() to availablePositions.removePosition(last()))
            .let { (currentPosition, nextAvailablePositions) ->
                //printBoard(this, availablePositions)
                //readln()
                when (currentPosition) {
                    availablePositions.endingPoint() -> {
                        //println("Trovato!")
                        //printBoard(this, availablePositions)
                        listOf(this)
                    }
                    else -> {
                        testedPaths += this.sorted()
                        currentPosition.nextSteps(nextAvailablePositions)
                            .map { this + it }
                            .filter { !it.isAlreadyTested() }
                            .flatMap { it.findNext(nextAvailablePositions) }
                    }
                }
            }

    fun part1(input: List<String>) = input.parseData()
        .let {
            listOf(it.startingPoint()).findNext(it.removePosition(it.startingPoint()))
                .let { result ->
                    println("paths: ${result.size}")
                    println("min: ${result.minOf { p -> p.getScore() }}")
                    //result.forEach { p -> printBoard(p, it) }
                }
        }


    fun part2(input: List<String>) = input.size

    val testInput = readInput("day15/Day15_test")
    part1(testInput).apply {
        println(this)
        //check(this == 8)
    }
    /*part2(testInput).apply {
        println(this)
        check(this == 8)
    }*/
}


data class Coordinates(val x: Int, val y: Int) {
    fun getSurrounding(width: Int, height: Int) = listOf(
        Coordinates(x, y + 1),
        Coordinates(x + 1, y),
        //Coordinates(x, y - 1),
        //Coordinates(x - 1, y),
    ).filter { x > -1 && x < width && y > -1 && y < height }

    override fun toString(): String {
        return "($x, $y)"
    }
}

data class Position(val coordinates: Coordinates, val riskLevel: Int) {
    fun getSurroundingPoints(fullData: List<Position>) =
        getSurroundingCoordinates(fullData)
            .let { p ->
                fullData.filter { x -> p.contains(x.coordinates) }
            }

    fun getSurroundingPoints(fullData: List<Position>, avoid: List<Position>) =
        getSurroundingCoordinates(fullData)
            .let { p ->
                fullData.filter { x -> p.contains(x.coordinates) }
            }.filter { !avoid.contains(it) }

    override fun toString(): String {
        return "[$coordinates->$riskLevel]"
    }

    private fun getSurroundingCoordinates(fullData: List<Position>) =
        coordinates.getSurrounding(fullData.maxOf { it.coordinates.x } + 1, fullData.maxOf { it.coordinates.y } + 1)
}

typealias Path = List<Position>
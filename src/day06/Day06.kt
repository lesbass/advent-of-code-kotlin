package day06

import readInput

fun main() {

    val oldFishTimeToChild = 6
    val newFishTimeToChild = oldFishTimeToChild + 2

    fun buildLanternFish(counter: Int, qty: Long) = counter to qty
    fun newLanternFishAndChild(qty: Long) = listOf(buildLanternFish(oldFishTimeToChild, qty), buildLanternFish(newFishTimeToChild, qty))
    fun decreaseLanternFishLife(counter: Int, qty: Long) = listOf(buildLanternFish(counter - 1, qty))

    fun processLanternFish(counter: Int, qty: Long) = if (counter == 0) {
        newLanternFishAndChild(qty)
    } else {
        decreaseLanternFishLife(counter, qty)
    }

    fun List<String>.parseLanternFishes() = first()
        .split(",")
        .groupBy { it.toInt() }
        .map { (counter, qty) -> buildLanternFish(counter, qty.size.toLong()) }

    fun part(input: List<String>, days: Int): Long = (1..days)
        .fold(input.parseLanternFishes())
        { acc, _ ->
            acc.flatMap { (counter, qty) -> processLanternFish(counter, qty) }
                .groupBy { (counter) -> counter }
                .map { (counter, value) -> counter to value.sumOf { (_, qty) -> qty } }
        }.sumOf { it.second }

    val input = readInput("day06/Day06")
    part(input, 80).apply {
        println(this)
        check(this == 350605.toLong())
    }
}
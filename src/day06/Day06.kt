package day06

import readInput

fun main() {

    fun List<String>.parseLanternFishes() = first()
        .split(",")
        .groupBy { it.toInt() }

    fun buildLanternFish(counter: Int, qty: Long) = counter to qty
    fun newLanternFishAndChild(qty: Long) = listOf(buildLanternFish(6, qty), buildLanternFish(8, qty))
    fun decreaseLanternFishLife(counter: Int, qty: Long) = listOf((counter - 1 to qty))
    fun getInitialFishes(input: List<String>) = input.parseLanternFishes()
        .map { (counter, qty) -> buildLanternFish(counter, qty.size.toLong()) }

    fun processLanternFish(counter: Int, qty: Long) = if (counter == 0) {
        newLanternFishAndChild(qty)
    } else {
        decreaseLanternFishLife(counter, qty)
    }

    fun part(input: List<String>, days: Int): Long = (1..days)
        .fold(getInitialFishes(input))
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
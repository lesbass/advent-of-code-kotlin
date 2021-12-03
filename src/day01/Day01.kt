package day01

import readInput

fun main() {
    fun Pair<Counter, Measurement>.getCounterValue() = first.value
    fun Pair<Counter, ThreeMeasurement>.getCounterValue() = first.value

    fun part1(input: List<String>): Int = input
        .map { Measurement(it.toInt()) }
        .foldIndexed(Counter.initial() to Measurement.initial())
        { index, (counter, prevMeasure), currMeasure ->
            counter.increaseIfTrue(index > 0 && currMeasure.value > prevMeasure.value) to currMeasure
        }.getCounterValue()

    fun part2(input: List<String>): Int = input
        .map { it.toInt() }
        .foldIndexed(Counter.initial() to ThreeMeasurement.initial())
        { index, (counter, prevMeasure), currMeasure ->
            counter.increaseIfTrue(
                index > 2 && prevMeasure.slide(currMeasure)
                    .getSum() > prevMeasure.getSum()
            ) to prevMeasure.slide(currMeasure)
        }.getCounterValue()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day01/Day01_test")
    check(part2(testInput) == 5)

    val input = readInput("day01/Day01")
    println(part1(input))
    println(part2(input))
}


data class Counter(val value: Int) {
    fun increaseIfTrue(expr: Boolean) = Counter(value + (if (expr) 1 else 0))

    companion object {
        fun initial() = Counter(0)
    }
}

data class Measurement(val value: Int) {
    companion object {
        fun initial() = Measurement(0)
    }
}

data class ThreeMeasurement(val first: Int, val second: Int, val third: Int) {
    fun slide(newThird: Int): ThreeMeasurement = ThreeMeasurement(this.second, this.third, newThird)
    fun getSum() = first + second + third

    companion object {
        fun initial() = ThreeMeasurement(0, 0, 0)
    }
}
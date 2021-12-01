data class ThreeMeasurement(val first: Int, val second: Int, val third: Int) {
    fun slide(newThird: Int): ThreeMeasurement = ThreeMeasurement(this.second, this.third, newThird)
    fun getSum() = first + second + third

    companion object {
        fun initial() = ThreeMeasurement(0, 0, 0)
    }
}

data class Counter(val value: Int) {
    operator fun plus(increment: Int): Counter {
        return Counter(value + increment)
    }

    companion object {
        fun initial() = Counter(0)
    }
}

data class Measurement(val value: Int) {
    companion object {
        fun initial() = Measurement(0)
    }
}

fun main() {
    fun part1(input: List<String>): Int = input
        .map { Measurement(it.toInt()) }
        .foldIndexed(Counter.initial() to Measurement.initial())
        { index, (counter, prevMeasure), currMeasure ->
            counter + (if (index > 0 && currMeasure.value > prevMeasure.value) 1 else 0) to currMeasure
        }.first.value

    fun part2(input: List<String>): Int = input
        .map { it.toInt() }
        .foldIndexed(Counter.initial() to ThreeMeasurement.initial())
        { index, (counter, prevMeasure), currMeasure ->
            counter + (if (index > 2 && prevMeasure.slide(currMeasure)
                    .getSum() > prevMeasure.getSum()
            ) 1 else 0) to prevMeasure.slide(currMeasure)
        }.first.value

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}

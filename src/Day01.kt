data class ThreeMeasurement(val first: Int, val second: Int, val third: Int) {
    fun slide(newThird: Int): ThreeMeasurement = ThreeMeasurement(this.second, this.third, newThird)
    fun getSum() = first + second + third
}

data class Counter(val value: Int) {
    operator fun plus(increment: Int): Counter {
        return Counter(value + increment)
    }
}

data class Measurement(val value: Int)

fun main() {
    fun part1(input: List<String>): Int = input
        .map { Measurement(it.toInt()) }
        .foldIndexed(Counter(0) to Measurement(0))
        { index, (counter, prev), curr ->
            counter + (if (index > 0 && curr.value > prev.value) 1 else 0) to curr
        }.first.value

    fun part2(input: List<String>): Int = input
        .map { it.toInt() }
        .foldIndexed(Counter(0) to ThreeMeasurement(0, 0, 0))
        { index, (counter, prev), i ->
            counter + (if (index > 2 && prev.slide(i).getSum() > prev.getSum()) 1 else 0) to prev.slide(i)
        }.first.value

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}

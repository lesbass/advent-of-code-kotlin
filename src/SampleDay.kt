fun main() {
    fun part1(input: List<String>) = input.size

    fun part2(input: List<String>) = input.size

    val testInput = readInput("dayXX/DayXX")
    part1(testInput).apply {
        println(this)
        check(this == 8)
    }
    part2(testInput).apply {
        println(this)
        check(this == 8)
    }
}

fun main() {
    fun part1(input: List<String>): Int = input.size

    fun part2(input: List<String>): Int = input.size

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 5)

    val input = readInput("day01")
    println(part1(input))
    println(part2(input))
}

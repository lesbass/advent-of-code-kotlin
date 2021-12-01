fun main() {
    fun part1(input: List<String>): Int = input
        .map { it.toInt() }
        .foldIndexed(Pair(0, 0))
        { index, (count, prev): Pair<Int, Int>, i -> Pair(count + (if (index > 0 && i > prev) 1 else 0), i) }
        .first


    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 7)

    val input = readInput("Day01")
    println(part1(input))
//    println(part2(input))
}

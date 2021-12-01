fun main() {
    fun part1(input: List<String>): Int = if (input.isEmpty()) 0
    else {
        var prev = input.first().toInt()
        input
            .map { it.toInt() }
            .drop(1)
            .count {
                if (it > prev) {
                    prev = it
                    true
                } else {
                    prev = it
                    false
                }
            }
    }


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

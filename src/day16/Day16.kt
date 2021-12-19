package day16

import readInput

fun List<Char>.joined() = joinToString("")
fun List<Char>.takeJoined(n: Int) = take(n).joined()
fun List<Char>.takeJoinedAsInt(n: Int) = takeJoined(n).toInt(2)

fun String.hex2bin() = asIterable().map { hex2binDict[it] }.joinToString("").toList()

fun List<Char>.getVersion() = takeJoinedAsInt(3) to drop(3)

fun List<Char>.getTypeId() = when (takeJoinedAsInt(3)) {
    0 -> TypeId.SUM
    1 -> TypeId.PRODUCT
    2 -> TypeId.MINIMUM
    3 -> TypeId.MAXIMUM
    4 -> TypeId.LITERAL
    5 -> TypeId.GREATER_THAN
    6 -> TypeId.LESS_THAN
    7 -> TypeId.EQUAL_TO
    else -> TypeId.UNKNOWN
} to drop(3)

fun List<Char>.getNumber() = drop(1).takeJoined(4) to drop(5)

fun List<Char>.getLiteral(num: String = ""): Pair<Long, List<Char>> =
    getNumber().let { (currNum, bits) ->
        if (first() == '0') (num + currNum).toLong(2) to bits
        else bits.getLiteral(num + currNum)
    }

fun List<Char>.getSubPacketLengthDataLength() = if (first() == '0') 15 else 11

fun processSubPacket(subType: Char, data: List<Char>, length: Int) =
    when (subType) {
        '0' -> data.take(length).readPackets().first to data.drop(length)
        else -> data.readPackets(length)
    }

fun List<Char>.getSubPackets(): Pair<List<Packet>, List<Char>> =
    drop(1).chunked(getSubPacketLengthDataLength()).let {
        if (it.isEmpty()) listOf<Packet>() to listOf()
        else processSubPacket(first(), it.drop(1).flatten(), it.first().joined().toInt(2))
    }

fun List<Char>.processOperator(version: Int, typeId: TypeId) =
    getSubPackets().let { (subPackets, remainingData) ->
        Packet(version, typeId, subPackets) to remainingData
    }

fun List<Char>.processLiteral(version: Int, typeId: TypeId) =
    getLiteral().let { (number, remainingData) ->
        Packet(version, typeId, listOf(), number) to remainingData
    }

fun List<Char>.readPacket(): Pair<Packet, List<Char>> =
    if (isEmpty()) Packet.empty() to this
    else getVersion().let { (version, dataMinusVersion) ->
        if (dataMinusVersion.isEmpty()) Packet.empty() to dataMinusVersion
        else dataMinusVersion.getTypeId().let { (typeId, dataMinusTypeId) ->
            when {
                dataMinusTypeId.isEmpty() -> Packet.empty() to dataMinusTypeId
                typeId == TypeId.LITERAL -> dataMinusTypeId.processLiteral(version, typeId)
                else -> dataMinusTypeId.processOperator(version, typeId)
            }
        }
    }

fun List<Char>.readPackets(
    takePackets: Int = 0,
    counter: Int = 0,
    packets: List<Packet> = listOf()
): Pair<List<Packet>, List<Char>> =
    readPacket().let { (packet, bits) ->
        if (bits.isEmpty() || takePackets > 0 && (counter + 1) == takePackets) {
            (packets + packet).filter { it.typeId != TypeId.UNKNOWN } to bits
        } else {
            bits.readPackets(takePackets, counter + 1, packets + packet)
        }
    }

fun main() {

    fun getAllPackets(input: List<String>) = input.first()
        .hex2bin()
        .readPackets().first

    fun part1(input: List<String>) = getAllPackets(input)
        .sumOf { p -> p.getSumOfVersions() }

    fun part2(input: List<String>) = getAllPackets(input)
        .map { x -> x.compute() }.first()

    val testInput = readInput("day16/Day16")
    part1(testInput).apply {
        println(this)
    }
    part2(testInput).apply {
        println(this)
    }
}

data class Packet(
    val version: Int = -1,
    val typeId: TypeId = TypeId.UNKNOWN,
    val subPackets: List<Packet> = listOf(),
    val number: Long = -1
) {
    fun getSumOfVersions(): Long = version + subPackets.sumOf { it.getSumOfVersions() }

    fun compute(): Long = when (typeId) {
        TypeId.LITERAL -> number
        TypeId.SUM -> subPackets.sumOf { it.compute() }
        TypeId.PRODUCT -> subPackets.fold(1.toLong()) { acc, packet -> acc * packet.compute() }
        TypeId.MINIMUM -> subPackets.minOf { it.compute() }
        TypeId.MAXIMUM -> subPackets.maxOf { it.compute() }
        TypeId.GREATER_THAN -> if (subPackets[0].compute() > subPackets[1].compute()) 1 else 0
        TypeId.LESS_THAN -> if (subPackets[0].compute() < subPackets[1].compute()) 1 else 0
        TypeId.EQUAL_TO -> if (subPackets[0].compute() == subPackets[1].compute()) 1 else 0
        else -> 0
    }

    companion object {
        fun empty() = Packet()
    }
}

enum class TypeId {
    SUM,            // 0
    PRODUCT,        // 1
    MINIMUM,        // 2
    MAXIMUM,        // 3
    LITERAL,        // 4
    GREATER_THAN,   // 5
    LESS_THAN,      // 6
    EQUAL_TO,       // 7
    UNKNOWN
}

val hex2binDict = mapOf(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111"
)
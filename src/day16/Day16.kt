package day16

import readInput

fun List<Char>.joined() = joinToString("")
fun List<Char>.takeJoined(n: Int) = take(n).joined()
fun List<Char>.takeJoinedAsInt(n: Int) = takeJoined(n).toInt(2)

fun String.hex2bin() = asIterable().map { hext2binDict[it] }.joinToString("").toList()

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

fun List<Char>.getLiteral(): Pair<Long, List<Char>> {
    var bits = this
    var num = ""
    var isLast = false
    while (!isLast) {
        isLast = bits.first() == '0'
        val (currNum, currBits) = bits.getNumber()
        num += currNum
        bits = currBits
    }

    return num.toLong(2) to bits
}

fun List<Char>.getSubPacketLengthDataLength() = if (first() == '0') 15 else 11

fun List<Char>.getSubPackets(): Pair<List<Packet>, List<Char>> =
    drop(1).chunked(getSubPacketLengthDataLength()).let {
        if (it.isEmpty()) return listOf<Packet>() to listOf()

        val subPacketsLengthValue = it.first().joined().toInt(2)
        val processingData = it.drop(1).flatten()

        return if (first() == '0') {
            processingData.take(subPacketsLengthValue).readPackets().first to processingData.drop(
                subPacketsLengthValue
            )
        } else {
            processingData.readPackets(subPacketsLengthValue)
        }
    }

fun List<Char>.readPacket(): Pair<Packet, List<Char>> {

    if (isEmpty()) return Packet.empty() to this
    val (version, dataMinusVersion) = getVersion()

    if (dataMinusVersion.isEmpty()) return Packet.empty() to dataMinusVersion
    val (typeId, dataMinusTypeId) = dataMinusVersion.getTypeId()


    if (dataMinusTypeId.isEmpty()) return Packet.empty() to dataMinusTypeId
    val (subPackets, dataMinusSubPackets) = if (typeId != TypeId.LITERAL) dataMinusTypeId.getSubPackets() else (listOf<Packet>() to dataMinusTypeId)

    val (number, dataMinusLiteral) = if (typeId == TypeId.LITERAL) dataMinusSubPackets.getLiteral() else 0.toLong() to dataMinusSubPackets

    return Packet(version, typeId, this, subPackets, number) to dataMinusLiteral
}

var serial = 0

fun List<Char>.readPackets(takePackets: Int = 0): Pair<List<Packet>, List<Char>> {
    val currSerial = serial++
    var bits = this
    val packets = mutableListOf<Packet>()
    var counter = 0
    while (bits.isNotEmpty()) {
        val packet = bits.readPacket()
        bits = packet.second
        packets += packet.first
        if (takePackets > 0 && ++counter == takePackets) break
    }
    return packets.filter { it.typeId != TypeId.UNKNOWN } to bits
}

fun main() {

    fun part1(input: List<String>) = input.first()
        .hex2bin()
        .readPackets()
        .let {
            it.first.sumOf { p -> p.getSumOfVersions() }
        }

    fun part2(input: List<String>) = input.first()
        .hex2bin()
        .readPackets()
        .let {
            it.first.map { x -> x.compute() }.first()
        }

    val testInput = readInput("day16/Day16")
    part1(testInput).apply {
        println(this)
        //check(this == 8)
    }

    check(part1(listOf("EE00D40C823060")) == 14.toLong())
    check(part1(listOf("38006F45291200")) == 9.toLong())
    check(part1(listOf("8A004A801A8002F478")) == 16.toLong())


    check(part2(listOf("C200B40A82")) == 3.toLong())
    check(part2(listOf("04005AC33890")) == 54.toLong())
    check(part2(listOf("880086C3E88112")) == 7.toLong())
    check(part2(listOf("CE00C43D881120")) == 9.toLong())
    check(part2(listOf("D8005AC2A8F0")) == 1.toLong())
    check(part2(listOf("F600BC2D8F")) == 0.toLong())
    check(part2(listOf("9C005AC2F8F0")) == 0.toLong())
    check(part2(listOf("9C0141080250320F1802104A08")) == 1.toLong())
    part2(testInput).apply {
        println(this)
        //check(this == 8)
    }


    /*part2(testInput).apply {
        println(this)
        check(this == 8)
    }*/
}

data class Packet(
    val version: Int = -1,
    val typeId: TypeId = TypeId.UNKNOWN,
    val data: List<Char> = listOf(),
    val subPackets: List<Packet> = listOf(),
    val number: Long = -1
) {
    fun getSumOfVersions(): Long = version + subPackets.sumOf { it.getSumOfVersions() }
    override fun toString(): String {
        return "\r\nPacket(version:$version, typeId=$typeId, number:$number, subPackets:${subPackets.size} " + (if (subPackets.any()) "-> \r\n\t" + subPackets.toString() +
                "\n\r " else "") + ")"
    }

    fun compute(): Long = when (typeId) {
        TypeId.LITERAL -> number.toLong()
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

val hext2binDict = mapOf(
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
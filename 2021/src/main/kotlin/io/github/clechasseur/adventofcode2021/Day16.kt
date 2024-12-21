package io.github.clechasseur.adventofcode2021

object Day16 {
    private const val data = "<REDACTED>"

    fun part1(): Int = bits().iterator().nextPacket().versionSum

    fun part2(): Long = bits().iterator().nextPacket().value

    private fun bits(): Sequence<Char> = data.asSequence().flatMap {
        it.toString().toInt(16).toString(2).padStart(4, '0').toList()
    }

    private abstract class Packet(val version: Int, val typeId: Int) {
        open val versionSum: Int
            get() = version

        abstract val value: Long
    }

    private class LiteralPacket(version: Int, val literalValue: Long) : Packet(version, 4) {
        override val value: Long
            get() = literalValue
    }

    private abstract class OperatorPacket(version: Int, typeId: Int, val subPackets: List<Packet>) : Packet(version, typeId) {
        override val versionSum: Int
            get() = super.versionSum + subPackets.sumOf { it.versionSum }
    }

    private class SumPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, 0, subPackets) {
        override val value: Long
            get() = subPackets.sumOf { it.value }
    }

    private class ProductPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, 1, subPackets) {
        override val value: Long
            get() = subPackets.fold(1L) { acc, p -> acc * p.value }
    }

    private class MinPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, 2, subPackets) {
        override val value: Long
            get() = subPackets.minOf { it.value }
    }

    private class MaxPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, 3, subPackets) {
        override val value: Long
            get() = subPackets.maxOf { it.value }
    }

    private class GreaterThanPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, 5, subPackets) {
        override val value: Long
            get() = if (subPackets[0].value > subPackets[1].value) 1L else 0L
    }

    private class LessThanPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, 6, subPackets) {
        override val value: Long
            get() = if (subPackets[0].value < subPackets[1].value) 1L else 0L
    }

    private class EqualToPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, 7, subPackets) {
        override val value: Long
            get() = if (subPackets[0].value == subPackets[1].value) 1L else 0L
    }

    private fun Iterator<Char>.nextPacket(): Packet {
        val version = nextValue(3)
        val typeId = nextValue(3)
        if (typeId == 4) {
            return LiteralPacket(version, nextLiteral())
        }

        val lengthTypeId = next()
        val subPackets = mutableListOf<Packet>()
        if (lengthTypeId == '0') {
            val subLength = nextValue(15)
            val subData = nextData(subLength)
            val subIt = subData.iterator()
            while (subIt.hasNext()) {
                subPackets.add(subIt.nextPacket())
            }
        } else {
            val numSubPackets = nextValue(11)
            for (i in 0 until numSubPackets) {
                subPackets.add(nextPacket())
            }
        }
        return when (typeId) {
            0 -> SumPacket(version, subPackets)
            1 -> ProductPacket(version, subPackets)
            2 -> MinPacket(version, subPackets)
            3 -> MaxPacket(version, subPackets)
            5 -> GreaterThanPacket(version, subPackets)
            6 -> LessThanPacket(version, subPackets)
            7 -> EqualToPacket(version, subPackets)
            else -> error("Wrong operator type ID: $typeId")
        }
    }

    private fun Iterator<Char>.nextData(size: Int): String {
        var s = ""
        for (i in 0 until size) {
            s += next()
        }
        return s
    }

    private fun Iterator<Char>.nextValue(size: Int): Int = nextData(size).toInt(2)

    private fun Iterator<Char>.nextLiteral(): Long {
        var s = ""
        while (true) {
            val prefix = next()
            s += nextData(4)
            if (prefix == '0') {
                break
            }
        }
        return s.toLong(2)
    }
}

import org.clechasseur.adventofcode2019.Day23Data
import org.clechasseur.adventofcode2019.IntcodeComputer

object Day23 {
    private val input = Day23Data.input

    fun part1(): Long {
        val computers = List(50) { idx -> IntcodeComputer(input, idx.toLong()) }
        val queues = List(50) { mutableListOf<Packet>() }
        var nat = emptyList<Packet>()
        while (nat.isEmpty()) {
            nat = readComputerInputs(computers, queues)
            writeQueuesToOutputs(computers, queues)
        }
        return nat.first().y
    }

    fun part2(): Long {
        val computers = List(50) { idx -> IntcodeComputer(input, idx.toLong()) }
        val queues = List(50) { mutableListOf<Packet>() }
        var natRam: Packet? = null
        val prevNatPackets = mutableListOf<Packet>()
        while (true) {
            val nat = readComputerInputs(computers, queues)
            if (nat.isNotEmpty()) {
                natRam = nat.last()
            }
            if (natRam != null && computers.none { it.done } && queues.all { it.isEmpty() }) {
                if (prevNatPackets.indexOfFirst { it.y == natRam!!.y } != -1) {
                    return natRam.y
                }
                prevNatPackets.add(natRam)
                computers[0].addInput(natRam.x, natRam.y)
                natRam = null
            }
            writeQueuesToOutputs(computers, queues)
        }
    }

    private data class Packet(val target: Int, val x: Long, val y: Long)

    private fun readComputerInputs(computers: List<IntcodeComputer>, queues: List<MutableList<Packet>>): List<Packet> {
        val nat = mutableListOf<Packet>()
        computers.forEachIndexed { idx, computer ->
            val output = computer.readAllOutput()
            require(output.size % 3 == 0) { "Output size of computer $idx is not divisible by 3" }
            output.chunked(3) { Packet(it[0].toInt(), it[1], it[2]) }.forEach { packet -> when {
                packet.target == 255 -> nat.add(packet)
                packet.target in queues.indices -> queues[packet.target].add(packet)
                else -> error("Packet destination ${packet.target} unknown")
            } }
        }
        return nat
    }

    private fun writeQueuesToOutputs(computers: List<IntcodeComputer>, queues: List<MutableList<Packet>>) {
        queues.forEachIndexed { idx, queue ->
            if (queue.isEmpty()) {
                computers[idx].addInput(-1L)
            } else {
                queue.forEach { packet ->
                    computers[idx].addInput(packet.x, packet.y)
                }
                queue.clear()
            }
        }
    }
}

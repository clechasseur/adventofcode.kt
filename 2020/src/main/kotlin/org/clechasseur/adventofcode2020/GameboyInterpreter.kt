package org.clechasseur.adventofcode2020

class GameboyInterpreter(val instructions: List<String>) {
    companion object {
        val instructionRegex = """^([a-z]{3}) ((?:\+|-)\d+)$""".toRegex()
    }

    var accumulator: Int = 0
        private set

    var ip: Int = 0
        private set

    val terminated
        get() = ip >= instructions.size

    fun step() {
        require(!terminated) { "Program is terminated" }
        val match = instructionRegex.matchEntire(instructions[ip]) ?: error("Wrong instruction: ${instructions[ip]}")
        val (op, arg) = match.destructured
        when (op) {
            "acc" -> {
                accumulator += arg.toInt()
                ip++
            }
            "jmp" -> ip += arg.toInt()
            "nop" -> ip++
            else -> error("Wrong opcode: $op")
        }
    }
}
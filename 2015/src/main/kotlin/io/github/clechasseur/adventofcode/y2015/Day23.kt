package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day23Data

object Day23 {
    private val input = Day23Data.input

    private val instructionRegex = """^(\w+) (.+)$""".toRegex()

    fun part1(): Long {
        val cpu = Cpu()
        Program(input).run(cpu)
        return cpu.get("b")
    }

    fun part2(): Long {
        val cpu = Cpu()
        cpu.set("a", 1L)
        Program(input).run(cpu)
        return cpu.get("b")
    }

    private class Cpu {
        private val registers = mutableMapOf<String, Long>()

        var ip: Int = 0

        fun get(register: String): Long = registers.getOrDefault(register, 0L)
        fun set(register: String, value: Long) {
            registers[register] = value
        }
    }

    private class Program(val instructions: List<Instruction>) {
        constructor(listing: String) : this(listing.lines().map { it.toInstruction() })

        fun run(cpu: Cpu) {
            while (cpu.ip in instructions.indices) {
                instructions[cpu.ip].execute(cpu)
            }
        }
    }

    private interface Instruction {
        fun execute(cpu: Cpu)
    }

    private class Hlf(val register: String) : Instruction {
        override fun execute(cpu: Cpu) {
            cpu.set(register, cpu.get(register) / 2L)
            cpu.ip += 1
        }
    }

    private class Tpl(val register: String) : Instruction {
        override fun execute(cpu: Cpu) {
            cpu.set(register, cpu.get(register) * 3L)
            cpu.ip += 1
        }
    }

    private class Inc(val register: String) : Instruction {
        override fun execute(cpu: Cpu) {
            cpu.set(register, cpu.get(register) + 1L)
            cpu.ip += 1
        }
    }

    private class Jmp(val offset: Int) : Instruction {
        override fun execute(cpu: Cpu) {
            cpu.ip += offset
        }
    }

    private class Jie(val register: String, val offset: Int) : Instruction {
        override fun execute(cpu: Cpu) {
            cpu.ip += if (cpu.get(register) % 2L == 0L) offset else 1
        }
    }

    private class Jio(val register: String, val offset: Int) : Instruction {
        override fun execute(cpu: Cpu) {
            cpu.ip += if (cpu.get(register) == 1L) offset else 1
        }
    }

    private fun String.toInstruction(): Instruction {
        val match = instructionRegex.matchEntire(this) ?: error("Wrong instruction: $this")
        val (opcode, rest) = match.destructured
        return when (opcode) {
            "hlf" -> Hlf(rest)
            "tpl" -> Tpl(rest)
            "inc" -> Inc(rest)
            "jmp" -> Jmp(rest.toInt())
            "jie" -> {
                val (r, offset) = rest.split(", ")
                Jie(r, offset.toInt())
            }
            "jio" -> {
                val (r, offset) = rest.split(", ")
                Jio(r, offset.toInt())
            }
            else -> error("Wrong opcode: $opcode")
        }
    }
}

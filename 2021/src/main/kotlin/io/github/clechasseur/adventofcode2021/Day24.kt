package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day24Data
import kotlin.math.floor

object Day24 {
    private val data = Day24Data.data

    fun part1(): Long {
        val largest = 52926995971999L
        require(aluMonad(largest)) { "ALU MONAD says model number is invalid" }
        require(kotlinMonad(largest.toString())) { "Kotlin MONAD says model number is invalid" }
        return largest
    }

    fun part2(): Long {
        val largest = 11811951311485L
        require(aluMonad(largest)) { "ALU MONAD says model number is invalid" }
        require(kotlinMonad(largest.toString())) { "Kotlin MONAD says model number is invalid" }
        return largest
    }

    private val inpRegex = """^inp (w|x|y|z)$""".toRegex()
    private val cmdRegex = """^(add|mul|div|mod|eql) (w|x|y|z) (w|x|y|z|-?\d+)$""".toRegex()

    // This is a working ALU:
    private class ALU(program: List<String>, input: String) {
        private val ip = program.iterator()
        private val cin = input.iterator()

        val ram = mutableMapOf(
            'w' to 0L,
            'x' to 0L,
            'y' to 0L,
            'z' to 0L,
        )

        fun execute() {
            while (ip.hasNext()) {
                executeNext()
            }
        }

        private fun executeNext() {
            val line = ip.next()
            val inp = inpRegex.matchEntire(line)
            if (inp != null) {
                require(cin.hasNext()) { "Empty input stream" }
                ram[inp.groupValues[1].first()] = cin.nextChar().toString().toLong()
            } else {
                val cmd = cmdRegex.matchEntire(line) ?: error("Wrong program line: $line")
                val (instruction, dest, src) = cmd.destructured
                val destValue = getValue(dest)
                val srcValue = getValue(src)
                when (instruction) {
                    "add" -> ram[dest.first()] = destValue + srcValue
                    "mul" -> ram[dest.first()] = destValue * srcValue
                    "div" -> ram[dest.first()] = floor(destValue.toDouble() / srcValue.toDouble()).toLong()
                    "mod" -> ram[dest.first()] = destValue % srcValue
                    "eql" -> ram[dest.first()] = if (destValue == srcValue) 1L else 0L
                    else -> error("Invalid instruction: $instruction")
                }
            }
        }

        private fun getValue(varOrVal: String): Long = if (varOrVal.length == 1 && varOrVal.first().isLetter()) {
            ram[varOrVal.first()] ?: error("Wrong variable: $varOrVal")
        } else varOrVal.toLong()
    }

    // This is the MONAD program executed by the ALU:
    private fun aluMonad(input: Long): Boolean {
        val alu = ALU(data.lines(), input.toString())
        alu.execute()
        return alu.ram['z'] == 0L
    }

    // This is the MONAD program converted to Kotlin and simplified a bit, with a few comments:
    private fun kotlinMonad(input: String): Boolean {
        val cin = input.iterator()
        fun nextDigit() = cin.next().toString().toLong()

        var w = 0L
        var x = 0L
        var y = 0L
        var z = 0L

        y = nextDigit() + 15L // first digit + 15
        z = y

        y = nextDigit() + 8L // second digit + 8
        z *= 26L
        z += y

        y = nextDigit() + 2L // third digit + 2
        z *= 26L
        z += y

        w = nextDigit()
        x = z
        x %= 26L // x == third digit + 2
        z /= 26L
        x -= 9L  // x == third digit - 7
        x = if (x == w) 1L else 0L
        x = if (x == 0L) 1L else 0L // third digit - 7 must be == fourth digit
        y = 25L
        y *= x
        y += 1L
        z *= y
        y = w + 6L // y == fourth digit + 6
        y *= x
        z += y

        y = nextDigit() + 13L // fifth digit + 13
        z *= 26L
        z += y

        y = nextDigit() + 4L // sixth digit + 4
        z *= 26L
        z += y

        y = nextDigit() + 1L // seventh digit + 1
        z *= 26L
        z += y

        w = nextDigit()
        x = z
        x %= 26L // x == seventh digit + 1
        z /= 26
        x -= 5L  // x == seventh digit - 4
        x = if (x == w) 1L else 0L
        x = if (x == 0L) 1L else 0L // seventh digit - 4 must be == eighth digit
        y = 25L
        y *= x
        y += 1L
        z *= y
        y = w + 9L // y == eighth digit + 9
        y *= x
        z += y

        y = nextDigit() + 5L // ninth digit + 5
        z *= 26L
        z += y

        w = nextDigit()
        x = z
        x %= 26L // x == ninth digit + 5
        z /= 26L
        x -= 7L  // x == ninth digit - 2
        x = if (x == w) 1L else 0L
        x = if (x == 0L) 1L else 0L // ninth digit - 2 must be == tenth digit
        y = 25L
        y *= x
        y += 1L
        z *= y
        y = w + 13L // y == tenth digit + 13
        y *= x
        z += y

        w = nextDigit()
        x = z
        x %= 26L // x == sixth digit + 4
        z /= 26L
        x -= 12L // x == sixth digit - 8
        x = if (x == w) 1L else 0L
        x = if (x == 0L) 1L else 0L // sixth digit - 8 must be == eleventh digit
        y = 25L
        y *= x
        y += 1L
        z *= y
        y = w + 9L // y == eleventh digit + 9
        y *= x
        z += y

        w = nextDigit()
        x = z
        x %= 26L // x == fifth digit + 13
        z /= 26L
        x -= 10L // x == fifth digit + 3
        x = if (x == w) 1L else 0L
        x = if (x == 0L) 1L else 0L // fifth digit + 3 must be == twelfth digit
        y = 25L
        y *= x
        y += 1L
        z *= y
        y = w + 6L // y == twelfth digit + 6
        y *= x
        z += y

        w = nextDigit()
        x = z
        x %= 26L // x == second digit + 8
        z /= 26L
        x -= 1L  // x == second digit + 7
        x = if (x == w) 1L else 0L
        x = if (x == 0L) 1L else 0L // second digit + 7 must be == thirteenth digit
        y = 25L
        y *= x
        y += 1L
        z *= y
        y = w + 2L // y == thirteenth digit + 2
        y *= x
        z += y

        w = nextDigit()
        x = z
        x %= 26L // x == first digit + 15
        z /= 26L
        x -= 11L // x == first digit + 4
        x = if (x == w) 1L else 0L
        x = if (x == 0L) 1L else 0L // first digit + 4 must be == fourteenth digit
        y = 25L
        y *= x
        y += 1L
        z *= y
        y = w + 2 // y == fourteenth digit + 2
        y *= x
        z += y

        return z == 0L
    }
}

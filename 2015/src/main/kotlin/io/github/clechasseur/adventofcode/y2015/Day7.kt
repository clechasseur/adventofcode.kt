package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day7Data

object Day7 {
    private val input = Day7Data.input

    private val valueRegex = """^([a-z]+|\d+) -> ([a-z]+)$""".toRegex()
    private val gateRegex = """^([a-z]+|\d+) (AND|OR|LSHIFT|RSHIFT) ([a-z]+|\d+) -> ([a-z]+)$""".toRegex()
    private val notRegex = """^NOT ([a-z]+|\d+) -> ([a-z]+)$""".toRegex()

    fun part1(): Int {
        val circuit = mutableMapOf<String, Wire>()
        input.lines().forEach { circuit.addInstruction(it) }
        return circuit.getWire("a").value
    }

    fun part2(): Int {
        val circuit = mutableMapOf<String, Wire>()
        input.lines().forEach { circuit.addInstruction(it) }
        circuit.getWire("b").source = Value(46065)
        return circuit.getWire("a").value
    }

    private interface Source {
        val value: Int
    }

    private class Value(override val value: Int) : Source {
        override fun toString(): String = value.toString()
    }

    private class Wire(val name: String, var source: Source? = null) : Source {
        override val value: Int by lazy { source?.value ?: 0 }

        override fun toString(): String = "Wire [$name]"
    }

    private class And(val left: Source, val right: Source) : Source {
        override val value: Int
            get() = left.value and right.value

        override fun toString(): String = "$left AND $right"
    }

    private class Or(val left: Source, val right: Source) : Source {
        override val value: Int
            get() = left.value or right.value

        override fun toString(): String = "$left OR $right"
    }

    private class Not(val source: Source) : Source {
        override val value: Int
            get() = source.value.inv()

        override fun toString(): String = "NOT $source"
    }

    private class LShift(val source: Source, val by: Int) : Source {
        override val value: Int
            get() = source.value shl by

        override fun toString(): String = "$source LSHIFT $by"
    }

    private class RShift(val source: Source, val by: Int) : Source {
        override val value: Int
            get() = source.value shr by

        override fun toString(): String = "$source RSHIFT $by"
    }

    private fun MutableMap<String, Wire>.getWire(name: String): Wire = computeIfAbsent(name) { Wire(name) }

    private fun MutableMap<String, Wire>.getSource(valueOrName: String): Source {
        val value = valueOrName.toIntOrNull()
        return if (value != null) Value(value) else getWire(valueOrName)
    }

    private fun MutableMap<String, Wire>.addInstruction(instruction: String) {
        val valueMatch = valueRegex.matchEntire(instruction)
        if (valueMatch != null) {
            val (valueOrName, wireName) = valueMatch.destructured
            getWire(wireName).source = getSource(valueOrName)
        } else {
            val notMatch = notRegex.matchEntire(instruction)
            if (notMatch != null) {
                val (valueOrName, destName) = notMatch.destructured
                getWire(destName).source = Not(getSource(valueOrName))
            } else {
                val gateMatch = gateRegex.matchEntire(instruction) ?: error("Wrong instruction: $instruction")
                val (leftValueOrName, gateName, rightValueOrName, destName) = gateMatch.destructured
                val leftSource = getSource(leftValueOrName)
                getWire(destName).source = when (gateName) {
                    "AND" -> And(leftSource, getSource(rightValueOrName))
                    "OR" -> Or(leftSource, getSource(rightValueOrName))
                    "LSHIFT" -> LShift(leftSource, rightValueOrName.toInt())
                    "RSHIFT" -> RShift(leftSource, rightValueOrName.toInt())
                    else -> error("Wrong gate name: $gateName")
                }
            }
        }
    }
}

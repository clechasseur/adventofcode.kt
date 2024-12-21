package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day21Data

object Day21 {
    private val input = Day21Data.input

    fun part1(): Long {
        val monkeys = input.toMonkeys()
        return monkeys["root"]!!.equation(monkeys).value.toLong()
    }

    fun part2(): Long {
        // I did this:
//        val monkeys = input.toMonkeys().toMutableMap()
//        monkeys["humn"] = Yeller("humn")
//        val root = monkeys["root"]!! as MathHead
//        val left = monkeys[root.monkey1]!!.equation(monkeys).value
//        val right = monkeys[root.monkey2]!!.equation(monkeys).value
//        println("$left == $right")
        // Then simplified by hand to this:
        return 0L // <REDACTED>
        // Not sure if it would've been quicker to actually implement a simplifier...
    }

    private val mathRegex = """(\w+) ([+*/-]) (\w+)""".toRegex()
    private val monkeyRegex = "(\\w+): (\\d+|${mathRegex.pattern})".toRegex()

    private val ops = mapOf<String, (Long, Long) -> Long>(
        "+" to { a, b -> a + b },
        "-" to { a, b -> a - b },
        "*" to { a, b -> a * b },
        "/" to { a, b -> a / b },
    )

    private sealed interface EqNode {
        val value: String
    }

    private class ValueNode(override val value: String) : EqNode

    private class OpNode(val left: EqNode, val right: EqNode, val op: String) : EqNode {
        override val value: String
            get() {
                val leftValue = left.value
                val rightValue = right.value
                val leftLongValue = leftValue.toLongOrNull()
                val rightLongValue = rightValue.toLongOrNull()
                return if (leftLongValue != null && rightLongValue != null) {
                    "${ops[op]!!(leftLongValue, rightLongValue)}L"
                } else {
                    "($leftValue $op $rightValue)"
                }
            }
    }

    private abstract class Monkey {
        abstract fun equation(monkeys: Map<String, Monkey>): EqNode
    }

    private class Yeller(val number: String) : Monkey() {
        override fun equation(monkeys: Map<String, Monkey>): EqNode = ValueNode(number)
    }

    private class MathHead(val monkey1: String, val monkey2: String, val op: String) : Monkey() {
        override fun equation(monkeys: Map<String, Monkey>): EqNode =
            OpNode(monkeys[monkey1]!!.equation(monkeys), monkeys[monkey2]!!.equation(monkeys), op)
    }

    private fun String.toMonkeys(): Map<String, Monkey> = lines().associate { line ->
        val match = monkeyRegex.matchEntire(line) ?: error("Wrong monkey line: $line")
        val (name, yell) = match.destructured
        val number = yell.toLongOrNull()
        if (number != null) {
            name to Yeller(number.toString())
        } else {
            val mathMatch = mathRegex.matchEntire(yell) ?: error("Wrong math yell: $yell")
            val (monkey1, op, monkey2) = mathMatch.destructured
            name to MathHead(monkey1, monkey2, op)
        }
    }
}

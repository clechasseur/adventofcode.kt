import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Day21 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val swapPositionRegex = """^swap position (\d+) with position (\d+)$""".toRegex()
    private val swapLettersRegex = """^swap letter (\w) with letter (\w)$""".toRegex()
    private val shiftRegex = """^rotate (left|right) (\d+) steps?$""".toRegex()
    private val shiftBasedOnRegex = """^rotate based on position of letter (\w)$""".toRegex()
    private val reverseRegex = """^reverse positions (\d+) through (\d+)$""".toRegex()
    private val moveRegex = """^move position (\d+) to position (\d+)$""".toRegex()

    fun part1(): String {
        val operations = input.lines().map { it.toOperation() }
        var password = "abcdefgh"
        for (operation in operations) {
            password = operation(password)
        }
        return password
    }

    fun part2(): String {
        val reverseOperations = input.lines().reversed().map { it.toOperation().reversed() }
        var password = "fbgdceah"
        for (operation in reverseOperations) {
            password = operation(password)
        }
        return password
    }

    private interface Operation {
        operator fun invoke(input: String): String
        fun reversed(): Operation
    }

    private class SwapPositions(a: Int, b: Int) : Operation {
        private val x = min(a, b)
        private val y = max(a, b)

        override fun invoke(input: String): String =
            "${input.substring(0, x)}${input[y]}${input.substring(x + 1, y)}${input[x]}${input.substring(y + 1)}"

        override fun reversed(): Operation = this

        override fun toString(): String = "swap position $x with position $y"
    }

    private class SwapLetters(val x: Char, val y: Char) : Operation {
        override fun invoke(input: String): String = SwapPositions(input.indexOf(x), input.indexOf(y))(input)

        override fun reversed(): Operation = this

        override fun toString(): String = "swap letter $x with letter $y"
    }

    private class Shift(val steps: Int) : Operation {
        override fun invoke(input: String): String {
            val startPos = if (steps >= 0) {
                steps % input.length
            } else {
                var moduloSteps = steps
                while (abs(moduloSteps) > input.length) {
                    moduloSteps += input.length
                }
                input.length + moduloSteps
            }
            return "${input.substring(startPos)}${input.substring(0, startPos)}"
        }

        override fun reversed(): Operation = Shift(-steps)

        override fun toString(): String = "rotate ${if (steps < 0) "right" else "left"} $steps steps"
    }

    private class ShiftBasedOn(val x: Char) : Operation {
        override fun invoke(input: String): String {
            val xPos = input.indexOf(x)
            return Shift(-(1 + xPos + if (xPos >= 4) 1 else 0))(input)
        }

        override fun reversed(): Operation = ShiftReversedBasedOn(x)

        override fun toString(): String = "rotate based on position of letter $x"
    }

    private class ShiftReversedBasedOn(val x: Char) : Operation {
        override fun invoke(input: String): String {
            for (i in 1..(input.length * 2)) {
                val candidate = Shift(i)(input)
                val candidatePos = candidate.indexOf(x)
                if (1 + candidatePos + (if (candidatePos >= 4) 1 else 0) == i) {
                    return candidate
                }
            }
            error("Could not reverse-rotate $input based on position of letter $x")
        }

        override fun reversed(): Operation {
            error("You probably didn't mean to reverse this")
        }

        override fun toString(): String = "reverse-rotate based on position of letter $x"
    }

    private class Reverse(a: Int, b: Int) : Operation {
        private val x = min(a, b)
        private val y = max(a, b)

        override fun invoke(input: String): String =
            "${input.substring(0, x)}${input.substring(x..y).reversed()}${input.substring(y + 1)}"

        override fun reversed(): Operation = this

        override fun toString(): String = "reverse positions $x through $y"
    }

    private class Move(val x: Int, val y: Int) : Operation {
        override fun invoke(input: String): String {
            val c = input[x]
            val without = "${input.substring(0, x)}${input.substring(x + 1)}"
            return if (y < without.length) {
                "${without.substring(0, y)}$c${without.substring(y)}"
            } else {
                without + c
            }
        }

        override fun reversed(): Operation = Move(y, x)

        override fun toString(): String = "move position $x to position $y"
    }

    private fun String.toOperation(): Operation {
        var match = swapPositionRegex.matchEntire(this)
        if (match != null) {
            return SwapPositions(match.groupValues[1].toInt(), match.groupValues[2].toInt())
        }

        match = swapLettersRegex.matchEntire(this)
        if (match != null) {
            return SwapLetters(match.groupValues[1].single(), match.groupValues[2].single())
        }

        match = shiftRegex.matchEntire(this)
        if (match != null) {
            var steps = match.groupValues[2].toInt()
            if (match.groupValues[1] == "right") {
                steps = -steps
            }
            return Shift(steps)
        }

        match = shiftBasedOnRegex.matchEntire(this)
        if (match != null) {
            return ShiftBasedOn(match.groupValues[1].single())
        }

        match = reverseRegex.matchEntire(this)
        if (match != null) {
            return Reverse(match.groupValues[1].toInt(), match.groupValues[2].toInt())
        }

        match = moveRegex.matchEntire(this)
        if (match != null) {
            return Move(match.groupValues[1].toInt(), match.groupValues[2].toInt())
        }

        error("Wrong operation: $this")
    }
}

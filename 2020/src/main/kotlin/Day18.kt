import org.clechasseur.adventofcode2020.Day18Data

object Day18 {
    private val input = Day18Data.input

    fun part1(): Long = input.lines().map { line ->
        line.withoutWhitespace.compute(advanced = false)
    }.sum()

    fun part2(): Long = input.lines().map { line ->
        line.withoutWhitespace.compute(advanced = true)
    }.sum()

    private interface Token
    private class ValueToken(val value: Long) : Token
    private class OperatorToken(val op: Char) : Token

    private val String.withoutWhitespace: String
        get() = "\\s".toRegex().replace(this, "")

    private fun String.compute(advanced: Boolean): Long {
        val tokens = mutableListOf<Token>()

        var (left, pos) = readValue(0, advanced)
        tokens.add(left)
        while (pos < length) {
            val op = OperatorToken(this[pos++])
            tokens.add(op)
            val (right, newPos) = readValue(pos, advanced)
            tokens.add(right)
            pos = newPos
        }

        return if (advanced) {
            tokens.computeAdvanced()
        } else {
            tokens.computeBasic()
        }
    }

    private fun String.readValue(pos: Int, advanced: Boolean): Pair<Token, Int> = if (this[pos] == '(') {
        var lastPos = pos + 1
        var depth = 1
        while (depth > 0) {
            depth += when (this[lastPos]) {
                '(' -> 1
                ')' -> -1
                else -> 0
            }
            lastPos++
        }
        ValueToken(substring(pos + 1 until lastPos - 1).compute(advanced)) to lastPos
    } else {
        ValueToken(this[pos].toString().toLong()) to pos + 1
    }

    private fun List<Token>.computeBasic(): Long {
        var value = (first() as ValueToken).value
        drop(1).chunked(2).forEach { (opToken, right) ->
            val rightValue = (right as ValueToken).value
            value = when (val op = (opToken as OperatorToken).op) {
                '+' -> value + rightValue
                '*' -> value * rightValue
                else -> error("Wrong operator: $op")
            }
        }
        return value
    }

    private fun List<Token>.computeAdvanced(): Long {
        val added = toMutableList()
        var i = 0
        while (i < (added.size - 1)) {
            val left = added[i]
            val op = added[i + 1]
            val right = added[i + 2]
            if ((op as OperatorToken).op == '+') {
                val result = (left as ValueToken).value + (right as ValueToken).value
                (0..2).forEach { _ -> added.removeAt(i) }
                added.add(i, ValueToken(result))
            } else {
                i += 2
            }
        }
        return added.computeBasic()
    }
}

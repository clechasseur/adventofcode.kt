import org.clechasseur.adventofcode2020.Day19Data

object Day19 {
    private val rules = Day19Data.rules
    private val messages = Day19Data.messages

    private val letterRuleRegex = """^(\d+): "(\w)"$""".toRegex()
    private val multiRuleRegex = """^(\d+): (\d+(?: \d+)*)$""".toRegex()
    private val orRuleRegex = """^(\d+): (\d+(?: \d+)*) \| (\d+(?: \d+)*)$""".toRegex()

    fun part1(): Int {
        val rulesMap = rules.toRules()
        val rule0 = rulesMap[0]!!.asRegexString(rulesMap).toRegex()
        return messages.lines().count { rule0.matches(it) }
    }

    fun part2(): Int {
        val rulesMap = rules.toRules().toMutableMap()
        rulesMap[8] = OneOrMoreRule(MultiRule(listOf(42)))
        rulesMap[11] = OrRule(listOf(
            MultiRule(listOf(42, 31)),
            MultiRule(listOf(42, 42, 31, 31)),
            MultiRule(listOf(42, 42, 42, 31, 31, 31)),
            MultiRule(listOf(42, 42, 42, 42, 31, 31, 31, 31))
        ))
        val rule0 = rulesMap[0]!!.asRegexString(rulesMap).toRegex()
        return messages.lines().count { rule0.matches(it) }
    }

    private interface Rule {
        fun asRegexString(rules: Map<Int, Rule>): String
    }

    private class LetterRule(val letter: Char) : Rule {
        override fun asRegexString(rules: Map<Int, Rule>): String = "$letter"
    }

    private class MultiRule(val multi: List<Int>) : Rule {
        override fun asRegexString(rules: Map<Int, Rule>): String =
            multi.joinToString("") { rules[it]!!.asRegexString(rules) }
    }

    private class OrRule(val alternatives: List<Rule>) : Rule {
        override fun asRegexString(rules: Map<Int, Rule>): String =
            alternatives.joinToString(separator = "|", prefix = "(", postfix = ")") {
                it.asRegexString(rules)
            }
    }

    private class OneOrMoreRule(val sub: Rule) : Rule {
        override fun asRegexString(rules: Map<Int, Rule>): String =
            "(${sub.asRegexString(rules)})+"
    }

    private fun String.toRules(): Map<Int, Rule> = lines().associate { line -> when {
        letterRuleRegex.matches(line) -> line.toLetterRule()
        multiRuleRegex.matches(line) -> line.toMultiRule()
        orRuleRegex.matches(line) -> line.toOrRule()
        else -> error("Not a valid rule: $line")
    } }

    private fun String.toLetterRule(): Pair<Int, Rule> {
        val match = letterRuleRegex.matchEntire(this) ?: error("Not a letter rule: $this")
        val (rule, letter) = match.destructured
        return rule.toInt() to LetterRule(letter.single())
    }

    private fun String.toMultiRule(): Pair<Int, Rule> {
        val match = multiRuleRegex.matchEntire(this) ?: error("Not a multi rule: $this")
        val (rule, multi) = match.destructured
        return rule.toInt() to MultiRule(multi.split(' ').map { it.toInt() })
    }

    private fun String.toOrRule(): Pair<Int, Rule> {
        val match = orRuleRegex.matchEntire(this) ?: error("Not an OR rule: $this")
        val (rule, eitherThisMulti, orThatMulti) = match.destructured
        return rule.toInt() to OrRule(listOf(
            MultiRule(eitherThisMulti.split(' ').map { it.toInt() }),
            MultiRule(orThatMulti.split(' ').map { it.toInt() })
        ))
    }
}

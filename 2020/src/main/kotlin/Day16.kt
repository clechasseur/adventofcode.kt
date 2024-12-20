import org.clechasseur.adventofcode2020.Day16Data

object Day16 {
    private val ticketRules = Day16Data.ticketRules
    private const val yourTicket = Day16Data.yourTicket
    private val nearbyTickets = Day16Data.nearbyTickets

    private val ticketRuleRegex = """^([a-z ]+): (\d+)-(\d+) or (\d+)-(\d+)$""".toRegex()

    fun part1(): Int {
        val rules = ticketRules.toRules()
        val ticketsToScan = nearbyTickets.lines().map { it.toTicket() }
        return ticketsToScan.scanningErrorRate(rules)
    }

    fun part2(): Long {
        val rules = ticketRules.toRules()
        val ticketsToScan = nearbyTickets.lines().map { it.toTicket() }.filter {
            it.scanningErrorRate(rules) == null
        }

        var orderedCandidates = ticketsToScan.first().fields.indices.map { fieldIdx ->
            rules.rules.filter { rule ->
                ticketsToScan.map { it.fields[fieldIdx] }.all { rule.valid(it) }
            }
        }

        val handled = mutableSetOf<Rule>()
        while (orderedCandidates.any { it.size > 1 }) {
            val lone = orderedCandidates.first {
                it.size == 1 && !handled.contains(it.single())
            }.single()
            orderedCandidates = orderedCandidates.map { candidates ->
                if (candidates.size == 1) candidates else candidates - lone
            }
            handled.add(lone)
        }
        val orderedRules = orderedCandidates.map { it.single() }

        val ticketToIdentify = yourTicket.toTicket()
        return orderedRules.withIndex().filter {
            it.value.field.startsWith("departure")
        }.map {
            ticketToIdentify.fields[it.index]
        }.fold(1L) { acc, i -> acc * i.toLong() }
    }

    private data class Rule(val field: String, val range1: IntRange, val range2: IntRange) {
        fun valid(value: Int): Boolean = value in range1 || value in range2
    }

    private class Rules(val rules: List<Rule>) {
        fun validForOne(value: Int): Boolean = rules.any { it.valid(value) }
    }

    private data class Ticket(val fields: List<Int>) {
        fun scanningErrorRate(rules: Rules): Int? = fields.firstOrNull { value ->
            !rules.validForOne(value)
        }
    }

    private fun List<Ticket>.scanningErrorRate(rules: Rules): Int = mapNotNull {
        it.scanningErrorRate(rules)
    }.sum()

    private fun String.toRules(): Rules = Rules(lines().map { rule ->
        val match = ticketRuleRegex.matchEntire(rule) ?: error("Wrong rule: $rule")
        val (field, r1f, r1l, r2f, r2l) = match.destructured
        Rule(field, r1f.toInt()..r1l.toInt(), r2f.toInt()..r2l.toInt())
    })

    private fun String.toTicket(): Ticket = Ticket(split(',').map { it.toInt() })
}

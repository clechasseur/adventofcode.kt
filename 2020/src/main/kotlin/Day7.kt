object Day7 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val bagsRegex = """^([a-z ]+) bags contain (no other bags|\d+ [a-z ]+ bags?(, \d+ [a-z ]+ bags?)*)\.$""".toRegex()
    private val numBagsRegex = """^(\d+) ([a-z ]+) bags?$""".toRegex()

    fun part1(): Int {
        val rules = input.toBagRules()
        return rules.bags.count { bag ->
            rules.iterateBag(bag).find { it.bagName == "shiny gold" } != null
        }
    }

    fun part2(): Int {
        val rules = input.toBagRules()
        val bag = rules.bags.find { it.bagName == "shiny gold" }!!
        return rules.iterateBag(bag).count()
    }

    private data class BagContent(val numBags: Int, val bagName: String)
    private data class Bag(val bagName: String, val content: List<BagContent>)
    private class BagRules(val bags: List<Bag>) {
        fun iterateBag(bag: Bag): Iterable<Bag> {
            val bagSeq = sequence {
                val content = mutableListOf(bag.content)
                while (content.isNotEmpty()) {
                    val cur = content.first()
                    content.removeAt(0)
                    cur.forEach { c ->
                        val b = bags.find { it.bagName == c.bagName }!!
                        (1..c.numBags).forEach { _ ->
                            yield(b)
                            content.add(b.content)
                        }
                    }
                }
            }
            return object : Iterable<Bag> {
                override fun iterator(): Iterator<Bag> = bagSeq.iterator()
            }
        }
    }

    private fun String.toBagRules() = BagRules(lineSequence().map { it.toBag() }.toList())

    private fun String.toBag(): Bag {
        val match = bagsRegex.matchEntire(this) ?: error("Wrong bag format: $this")
        val bagName = match.groupValues[1]
        val content = mutableListOf<BagContent>()
        if (match.groupValues[2] != "no other bags") {
            match.groupValues[2].split(", ").forEach { c ->
                val contentMatch = numBagsRegex.matchEntire(c) ?: error("Wrong content format: $c")
                content.add(BagContent(
                    contentMatch.groupValues[1].toInt(),
                    contentMatch.groupValues[2]
                ))
            }
        }
        return Bag(bagName, content)
    }
}
import org.clechasseur.adventofcode2016.md5

object Day14 {
    private const val input = "<REDACTED>"

    private val tripleRegex = """(.)\1\1""".toRegex()

    fun part1(): Int = keySequence(false).drop(63).first().second

    fun part2(): Int = keySequence(true).drop(63).first().second

    private fun hashSequence(stretch: Boolean): Sequence<String> = generateSequence(0) { it + 1 }.map { i ->
        generateSequence("$input$i") { md5(it) }.drop(if (stretch) 2017 else 1).first()
    }

    private fun keySequence(stretch: Boolean): Sequence<Pair<String, Int>> = KeyDetector(stretch).keySequence()

    private class KeyDetector(stretch: Boolean) {
        private val hashIt = hashSequence(stretch).iterator()
        private val hashes = mutableListOf<String>()
        private var i = 0

        fun keySequence(): Sequence<Pair<String, Int>> = sequence {
            while (true) {
                while (i + 1000 >= hashes.size) {
                    fill()
                }
                val candidateKey = hashes[i]
                val tripleMatch = tripleRegex.find(candidateKey)
                if (tripleMatch != null) {
                    val quintupleRegex = tripleMatch.groupValues[1].repeat(5).toRegex()
                    if (next1000().any { quintupleRegex.containsMatchIn(it) }) {
                        yield(candidateKey to i)
                    }
                }
                i++
            }
        }

        private fun fill() {
            (0 until 2000).forEach { _ -> hashes.add(hashIt.next()) }
        }

        private fun next1000(): Sequence<String> = generateSequence(i + 1) { it + 1 }.take(1000).map {
            hashes[it]
        }
    }
}

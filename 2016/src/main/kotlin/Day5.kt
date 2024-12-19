import java.math.BigInteger
import java.security.MessageDigest

object Day5 {
    private const val input = "<REDACTED>"

    fun part1() = md5Sequence().filter { it.startsWith("00000") }.map { it[5] }.take(8).joinToString("")

    fun part2(): String {
        val password = MutableList(8) { ' ' }
        val hashIt = md5Sequence().filter { it.startsWith("00000") }.iterator()
        while (password.any { it == ' ' }) {
            val nextInterestingHash = hashIt.next()
            if (nextInterestingHash[5] in '0'..'7') {
                val idx = nextInterestingHash[5].toString().toInt()
                if (password[idx] == ' ') {
                    password[idx] = nextInterestingHash[6]
                }
            }
        }
        return password.joinToString("")
    }

    private fun md5Sequence() = generateSequence(0L) { it + 1L }.map { idx ->
        val digest = MessageDigest.getInstance("MD5").digest("$input$idx".toByteArray())
        BigInteger(1, digest).toString(16).padStart(32, '0')
    }
}

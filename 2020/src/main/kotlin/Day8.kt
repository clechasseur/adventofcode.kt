import org.clechasseur.adventofcode2020.GameboyInterpreter

object Day8 {
    private val input = """
        <REDACTED>
    """.trimIndent().lines()

    fun part1(): Int {
        val gameboy = GameboyInterpreter(input)
        val ips = mutableSetOf<Int>()
        while (!ips.contains(gameboy.ip)) {
            ips.add(gameboy.ip)
            gameboy.step()
        }
        return gameboy.accumulator
    }

    fun part2(): Int {
        for (i in input.indices) {
            val inputCopy = input.toMutableList()
            when {
                inputCopy[i].startsWith("jmp") -> {
                    inputCopy[i] = inputCopy[i].replace("jmp", "nop")
                }
                inputCopy[i].startsWith("nop") -> {
                    inputCopy[i] = inputCopy[i].replace("nop", "jmp")
                }
            }
            if (inputCopy[i] != input[i]) {
                val gameboy = GameboyInterpreter(inputCopy)
                val ips = mutableSetOf<Int>()
                while (!ips.contains(gameboy.ip) && !gameboy.terminated) {
                    ips.add(gameboy.ip)
                    gameboy.step()
                }
                if (gameboy.terminated) {
                    return gameboy.accumulator
                }
            }
        }
        error("Could not fix program")
    }
}
object Day10 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val giveRegex = """^value (\d+) goes to bot (\d+)$""".toRegex()
    private val botRegex = """^bot (\d+) gives low to (\w+) (\d+) and high to (\w+) (\d+)$""".toRegex()

    fun part1(): Int {
        val instructions = input.lines().map { it.toInstruction() }
        val env = Environment()

        instructions.filterIsInstance(SetValueToBot::class.java).forEach { it(env) }

        val goodNum: (Environment) -> Int = { e ->
            e.bots.filter { (_, bot) ->
                bot.ready && bot.high == 61 && bot.low == 17
            }.asSequence().singleOrNull()?.key ?: -1
        }
        while (goodNum(env) == -1) {
            instructions.filterIsInstance(MakeBotHandItsValue::class.java).forEach { instruction ->
                if (env.getBot(instruction.botNum).ready) {
                    instruction(env)
                }
            }
        }
        return goodNum(env)
    }

    fun part2(): Int {
        val instructions = input.lines().map { it.toInstruction() }
        val env = Environment()

        instructions.filterIsInstance(SetValueToBot::class.java).forEach { it(env) }

        val result: (Environment) -> Int = { e ->
            (e.outputs[0] ?: 0) * (e.outputs[1] ?: 0) * (e.outputs[2] ?: 0)
        }
        while (result(env) == 0) {
            instructions.filterIsInstance(MakeBotHandItsValue::class.java).forEach { instruction ->
                if (env.getBot(instruction.botNum).ready) {
                    instruction(env)
                }
            }
        }
        return result(env)
    }

    private class Bot {
        private val values = mutableSetOf<Int>()

        val ready: Boolean
            get() = values.size == 2

        val high: Int
            get() = values.max()!!
        val low: Int
            get() = values.min()!!

        fun give(value: Int) {
            values.add(value)
        }
    }

    private class Environment {
        private val _bots = mutableMapOf<Int, Bot>()
        private val _outputs = mutableMapOf<Int, Int>()

        val bots: Map<Int, Bot>
            get() = _bots

        val outputs: Map<Int, Int>
            get() = _outputs

        fun getBot(num: Int) = _bots.getOrPut(num, { Bot() })

        fun setOutput(num: Int, value: Int) {
            _outputs[num] = value
        }
    }

    private enum class Destination {
        BOT, OUTPUT
    }

    private interface Instruction {
        operator fun invoke(env: Environment)
    }

    private class SetValueToBot(val value: Int, val botNum: Int) : Instruction {
        override fun invoke(env: Environment) {
            env.getBot(botNum).give(value)
        }
    }

    private class MakeBotHandItsValue(val botNum: Int, val destLow: Destination, val destLowNum: Int,
                                      val destHigh: Destination, val destHighNum: Int) : Instruction {

        override fun invoke(env: Environment) {
            val bot = env.getBot(botNum)
            saveToDestination(env, bot.low, destLow, destLowNum)
            saveToDestination(env, bot.high, destHigh, destHighNum)
        }

        private fun saveToDestination(env: Environment, value: Int, dest: Destination, num: Int) = when (dest) {
            Destination.BOT -> env.getBot(num).give(value)
            Destination.OUTPUT -> env.setOutput(num, value)
        }
    }

    private fun String.toInstruction(): Instruction {
        val giveMatch = giveRegex.matchEntire(this)
        if (giveMatch != null) {
            return SetValueToBot(giveMatch.groupValues[1].toInt(), giveMatch.groupValues[2].toInt())
        }

        val botMatch = botRegex.matchEntire(this) ?: error("Invalid instruction")
        return MakeBotHandItsValue(
                botMatch.groupValues[1].toInt(),
                Destination.valueOf(botMatch.groupValues[2].toUpperCase()),
                botMatch.groupValues[3].toInt(),
                Destination.valueOf(botMatch.groupValues[4].toUpperCase()),
                botMatch.groupValues[5].toInt()
        )
    }
}

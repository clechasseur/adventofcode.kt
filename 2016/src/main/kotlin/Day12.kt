object Day12 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val instructionRegex = """^(\w{3}) (-?\d+|[abcd])(?: (-?\d+|[abcd]))?""".toRegex()

    fun part1(): Int {
        val cpu = Cpu()
        cpu.execute(Program(input))
        return cpu.get("a")
    }

    fun part2(): Int {
        val cpu = Cpu()
        cpu.set("c", 1)
        cpu.execute(Program(input))
        return cpu.get("a")
    }

    private class Cpu {
        private val registers = mutableMapOf<String, Int>()

        var ip: Int = 0

        fun get(register: String): Int = registers.getOrDefault(register, 0)
        fun set(register: String, value: Int) {
            registers[register] = value
        }
    }

    private interface Instruction {
        operator fun invoke(cpu: Cpu)
    }

    private interface Value {
        fun getValue(cpu: Cpu): Int
    }

    private class Program(val instructions: List<Instruction>) {
        constructor(program: String) : this(program.lines().map { it.toInstruction() })
    }

    private fun Cpu.execute(program: Program) {
        while (ip in program.instructions.indices) {
            program.instructions[ip](this)
        }
    }

    private class IntValue(val value: Int) : Value {
        override fun getValue(cpu: Cpu): Int = value
    }

    private class RegisterValue(val register: String) : Value {
        override fun getValue(cpu: Cpu): Int = cpu.get(register)
    }

    private class Cpy(val value: Value, val register: String) : Instruction {
        override fun invoke(cpu: Cpu) {
            cpu.set(register, value.getValue(cpu))
            cpu.ip++
        }
    }

    private class Inc(val register: String) : Instruction {
        override fun invoke(cpu: Cpu) {
            cpu.set(register, cpu.get(register) + 1)
            cpu.ip++
        }
    }

    private class Dec(val register: String) : Instruction {
        override fun invoke(cpu: Cpu) {
            cpu.set(register, cpu.get(register) - 1)
            cpu.ip++
        }
    }

    private class Jnz(val value: Value, val offset: Int) : Instruction {
        override fun invoke(cpu: Cpu) {
            cpu.ip += if (value.getValue(cpu) != 0) offset else 1
        }
    }

    private fun String.toValue(): Value {
        val value = toIntOrNull()
        return if (value != null) {
            IntValue(value)
        } else {
            RegisterValue(this)
        }
    }

    private fun String.toInstruction(): Instruction {
        val match = instructionRegex.matchEntire(this) ?: error("Wrong instruction: $this")
        return when (match.groupValues[1]) {
            "cpy" -> Cpy(match.groupValues[2].toValue(), match.groupValues[3])
            "inc" -> Inc(match.groupValues[2])
            "dec" -> Dec(match.groupValues[2])
            "jnz" -> Jnz(match.groupValues[2].toValue(), match.groupValues[3].toInt())
            else -> error("Wrong opcode: ${match.groupValues[1]}")
        }
    }
}

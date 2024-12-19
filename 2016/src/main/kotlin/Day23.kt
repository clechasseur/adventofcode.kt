import Day23.toValue

object Day23 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val instructionRegex = """^(\w{3}) (-?\d+|[abcd])(?: (-?\d+|[abcd]))?""".toRegex()

    fun part1(): Int {
        val cpu = Cpu()
        cpu.set("a", 7)
        cpu.execute(Program(input))
        return cpu.get("a")
    }

    fun part2(): Int {
        val cpu = Cpu()
        cpu.set("a", 12)
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
        operator fun invoke(cpu: Cpu, program: Program)
        fun toggle(): Instruction
    }

    private interface Value {
        val registerName: String?

        fun getValue(cpu: Cpu): Int
    }

    private class Program(val instructions: MutableList<Instruction>) {
        constructor(program: String) : this(program.lines().map { it.toInstruction() }.toMutableList())
    }

    private fun Cpu.execute(program: Program) {
        while (ip in program.instructions.indices) {
            program.instructions[ip](this, program)
        }
    }

    private class IntValue(val value: Int) : Value {
        override val registerName: String?
            get() = null

        override fun getValue(cpu: Cpu): Int = value
    }

    private class RegisterValue(val register: String) : Value {
        override val registerName: String
            get() = register

        override fun getValue(cpu: Cpu): Int = cpu.get(register)
    }

    private class Cpy(val value: Value, val register: Value) : Instruction {
        override fun invoke(cpu: Cpu, program: Program) {
            val registerName = register.registerName
            if (registerName != null) {
                cpu.set(registerName, value.getValue(cpu))
            }
            cpu.ip++
        }

        override fun toggle(): Instruction = Jnz(value, register)
    }

    private class Inc(val register: Value) : Instruction {
        override fun invoke(cpu: Cpu, program: Program) {
            val registerName = register.registerName
            if (registerName != null) {
                cpu.set(registerName, cpu.get(registerName) + 1)
            }
            cpu.ip++
        }

        override fun toggle(): Instruction = Dec(register)
    }

    private class Dec(val register: Value) : Instruction {
        override fun invoke(cpu: Cpu, program: Program) {
            val registerName = register.registerName
            if (registerName != null) {
                cpu.set(registerName, cpu.get(registerName) - 1)
            }
            cpu.ip++
        }

        override fun toggle(): Instruction = Inc(register)
    }

    private class Jnz(val value: Value, val offset: Value) : Instruction {
        override fun invoke(cpu: Cpu, program: Program) {
            cpu.ip += if (value.getValue(cpu) != 0) offset.getValue(cpu) else 1
        }

        override fun toggle(): Instruction = Cpy(value, offset)
    }

    private class Tgl(val offset: Value) : Instruction {
        override fun invoke(cpu: Cpu, program: Program) {
            val toToggle = cpu.ip + offset.getValue(cpu)
            if (toToggle in program.instructions.indices) {
                program.instructions[toToggle] = program.instructions[toToggle].toggle()
            }
            cpu.ip++
        }

        override fun toggle(): Instruction = Inc(offset)
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
            "cpy" -> Cpy(match.groupValues[2].toValue(), match.groupValues[3].toValue())
            "inc" -> Inc(match.groupValues[2].toValue())
            "dec" -> Dec(match.groupValues[2].toValue())
            "jnz" -> Jnz(match.groupValues[2].toValue(), match.groupValues[3].toValue())
            "tgl" -> Tgl(match.groupValues[2].toValue())
            else -> error("Wrong opcode: ${match.groupValues[1]}")
        }
    }
}

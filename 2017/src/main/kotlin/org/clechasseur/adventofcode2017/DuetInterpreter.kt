package org.clechasseur.adventofcode2017

class DuetInterpreter(programText: String, private val dual: Boolean, private val release: Boolean = false) {
    private val program = programText.lineSequence().map { it.toInstruction() }.toList()
    private val ram: List<Memory> = when {
        dual -> listOf(Memory(0), Memory(1))
        else -> listOf(Memory(0))
    }

    private val terminated: Boolean
        get() = ram.any { it.ip !in program.indices }

    private val deadlocked: Boolean
        get() = ram.all { it.waiting && it.queue.isEmpty() }

    var recovered: Long? = null
        private set

    init {
        if (dual) {
            runDual()
        } else {
            runSingle()
        }
    }

    fun getSndCount(id: Int): Int = ram[id].sndCount

    fun getMulCount(id: Int): Int = ram[id].mulCount

    fun getReg(id: Int, reg: Char): Long = ram[id].get(reg)

    private fun runSingle() {
        while (!terminated && recovered == null) {
            program[ram.single().ip](ram.single())
            ram.single().ip++
        }
    }

    private fun runDual() {
        while (!terminated && !deadlocked) {
            ram.forEach { mem ->
                mem.waiting = false
                while (mem.ip in program.indices && !mem.waiting) {
                    program[mem.ip](mem)
                    if (!mem.waiting) {
                        mem.ip++
                    }
                }
            }
        }
    }

    private inner class Memory(val id: Int) {
        var ip = 0
        val registers = mutableMapOf<Char, Long>()
        val queue = mutableListOf<Long>()
        var waiting = false
        var sndCount = 0
        var mulCount = 0

        init {
            if (dual) {
                registers['p'] = id.toLong()
            }
            if (release) {
                registers['a'] = 1
            }
        }

        fun get(reg: Char): Long = registers[reg] ?: 0L

        fun push(value: Long) {
            queue.add(value)
        }

        fun pop(): Long? {
            if (queue.isEmpty()) {
                waiting = true
                return null
            }
            val value = queue.first()
            queue.removeAt(0)
            return value
        }
    }

    private interface Instruction {
        operator fun invoke(ram: Memory)
    }

    private fun String.toInstruction(): Instruction = when {
        startsWith("snd ") -> Snd(split(' ')[1].toFetcher())
        startsWith("set ") -> {
            val (reg, value) = split(' ').drop(1)
            Set(reg.first(), value.toFetcher())
        }
        startsWith("add ") -> {
            val (reg, diff) = split(' ').drop(1)
            Add(reg.first(), diff.toFetcher())
        }
        startsWith("sub ") -> {
            val (reg, diff) = split(' ').drop(1)
            Sub(reg.first(), diff.toFetcher())
        }
        startsWith("mul ") -> {
            val (reg, quotient) = split(' ').drop(1)
            Mul(reg.first(), quotient.toFetcher())
        }
        startsWith("mod ") -> {
            val (reg, divisor) = split(' ').drop(1)
            Mod(reg.first(), divisor.toFetcher())
        }
        startsWith("rcv ") -> Rcv(split(' ')[1].first())
        startsWith("jgz ") -> {
            val (check, offset) = split(' ').drop(1)
            Jgz(check.toFetcher(), offset.toFetcher())
        }
        startsWith("jnz ") -> {
            val (check, offset) = split(' ').drop(1)
            Jnz(check.toFetcher(), offset.toFetcher())
        }
        else -> error("Unknown instruction: $this")
    }

    private interface Fetcher {
        fun fetchValue(ram: Memory): Long
    }

    private fun String.toFetcher(): Fetcher = when {
        first().isLetter() -> RegisterFetcher(first())
        else -> ImmediateFetcher(toLong())
    }

    private inner class Snd(val frequency: Fetcher) : Instruction {
        override fun invoke(ram: Memory) {
            val ramPos = when {
                dual -> 1 - ram.id
                else -> 0
            }
            this@DuetInterpreter.ram[ramPos].push(frequency.fetchValue(ram))
            ram.sndCount++
        }

        override fun toString(): String = "snd $frequency"
    }

    private class Set(val reg: Char, val value: Fetcher) : Instruction {
        override fun invoke(ram: Memory) {
            ram.registers[reg] = value.fetchValue(ram)
        }

        override fun toString(): String = "set $reg $value"
    }

    private class Add(val reg: Char, val diff: Fetcher): Instruction {
        override fun invoke(ram: Memory) {
            ram.registers[reg] = ram.get(reg) + diff.fetchValue(ram)
        }

        override fun toString(): String = "add $reg $diff"
    }

    private class Sub(val reg: Char, val diff: Fetcher): Instruction {
        override fun invoke(ram: Memory) {
            ram.registers[reg] = ram.get(reg) - diff.fetchValue(ram)
        }

        override fun toString(): String = "sub $reg $diff"
    }

    private class Mul(val reg: Char, val quotient: Fetcher): Instruction {
        override fun invoke(ram: Memory) {
            ram.registers[reg] = ram.get(reg) * quotient.fetchValue(ram)
            ram.mulCount++
        }

        override fun toString(): String = "mul $reg $quotient"
    }

    private class Mod(val reg: Char, val divisor: Fetcher): Instruction {
        override fun invoke(ram: Memory) {
            ram.registers[reg] = ram.get(reg) % divisor.fetchValue(ram)
        }

        override fun toString(): String = "mod $reg $divisor"
    }

    private inner class Rcv(val reg: Char): Instruction {
        override fun invoke(ram: Memory) {
            if (dual) {
                val value = ram.pop()
                if (value != null) {
                    ram.registers[reg] = value
                }
            } else {
                if (ram.get(reg) != 0L) {
                    while (ram.queue.isNotEmpty()) {
                        recovered = ram.pop() ?: error("Trying to recover sound but none was played")
                    }
                }
            }
        }

        override fun toString(): String = "rcv $reg"
    }

    private class Jgz(val check: Fetcher, val offset: Fetcher): Instruction {
        override fun invoke(ram: Memory) {
            if (check.fetchValue(ram) > 0L) {
                ram.ip += offset.fetchValue(ram).toInt() - 1
            }
        }

        override fun toString(): String = "jgz $check $offset"
    }

    private class Jnz(val check: Fetcher, val offset: Fetcher): Instruction {
        override fun invoke(ram: Memory) {
            if (check.fetchValue(ram) != 0L) {
                ram.ip += offset.fetchValue(ram).toInt() - 1
            }
        }

        override fun toString(): String = "jnz $check $offset"
    }

    private class ImmediateFetcher(val value: Long): Fetcher {
        override fun fetchValue(ram: Memory): Long = value

        override fun toString(): String = value.toString()
    }

    private class RegisterFetcher(val reg: Char): Fetcher {
        override fun fetchValue(ram: Memory): Long = ram.get(reg)

        override fun toString(): String = reg.toString()
    }
}
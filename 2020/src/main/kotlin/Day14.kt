import kotlin.math.pow

object Day14 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val maskRegex = """^mask = ((?:0|1|X){36})$""".toRegex()
    private val assignRegex = """^mem\[(\d+)] = (\d+)$""".toRegex()

    fun part1(): Long {
        val cpu = Computer()
        val program = input.lineSequence().map { it.toInstruction() }
        program.forEach { it(cpu) }
        return cpu.ram.values.sum()
    }

    fun part2(): Long {
        val cpu = Computer2()
        val program = input.lineSequence().map { it.toInstruction2() }
        program.forEach { it(cpu) }
        return cpu.ram.values.sum()
    }

    private class Computer {
        var yesMask = 0L
        var noMask = 0L
        val ram = mutableMapOf<Long, Long>()
    }

    private interface Instruction {
        operator fun invoke(cpu: Computer)
    }

    private class SetMask(mask: String) : Instruction {
        private val yesMask = mask.replace('X', '0').toLong(2)
        private val noMask = mask.replace('X', '1').toLong(2)

        override fun invoke(cpu: Computer) {
            cpu.yesMask = yesMask
            cpu.noMask = noMask
        }
    }

    private class Assign(val address: Long, val value: Long) : Instruction {
        override fun invoke(cpu: Computer) {
            cpu.ram[address] = (value or cpu.yesMask) and cpu.noMask
        }
    }

    private fun String.toInstruction(): Instruction = when (val maskMatch = maskRegex.matchEntire(this)) {
        null -> {
            val assignMatch = assignRegex.matchEntire(this)
            require(assignMatch != null) { "Invalid instruction: $this" }
            Assign(
                address = assignMatch.groupValues[1].toLong(),
                value = assignMatch.groupValues[2].toLong()
            )
        }
        else -> SetMask(maskMatch.groupValues[1])
    }

    private class FloatingMask(mask: String) {
        val bitPos = mask.indices.map { if (mask.reversed()[it] == '1') it else -1 }.filter { it >= 0 }
        val maxValue = mask.toLong(2)
    }

    private class FloatingValue(private val bits: Long, val mask: FloatingMask) {
        val longValue: Long
            get() = mask.bitPos.indices.map { bitIdx ->
                if (2.0.pow(bitIdx).toLong() and bits != 0L) 2.0.pow(mask.bitPos[bitIdx]).toLong() else 0L
            }.sum()

        val next: FloatingValue
            get() {
                require(longValue < mask.maxValue) { "Max value reached: $longValue" }
                return FloatingValue(bits + 1, mask)
            }

        constructor(mask: FloatingMask) : this(0L, mask)
    }

    private class Computer2 {
        var yesMask = 0L
        var floatMask: FloatingMask? = null
        val ram = mutableMapOf<Long, Long>()
    }

    private interface Instruction2 {
        operator fun invoke(cpu: Computer2)
    }

    private class SetMask2(mask: String) : Instruction2 {
        private val yesMask = mask.replace('X', '0').toLong(2)
        private val floatMask = mask.replace("\\d".toRegex(), "0").replace('X', '1')

        override fun invoke(cpu: Computer2) {
            cpu.yesMask = yesMask
            cpu.floatMask = FloatingMask(floatMask)
        }
    }

    private class Assign2(val address: Long, val value: Long) : Instruction2 {
        override fun invoke(cpu: Computer2) {
            require(cpu.floatMask != null) { "Must have a mask" }
            var addr = FloatingValue(cpu.floatMask!!)
            while (true) {
                cpu.ram[((address or cpu.yesMask) and cpu.floatMask!!.maxValue.inv()) + addr.longValue] = value
                if (addr.longValue < addr.mask.maxValue) {
                    addr = addr.next
                } else {
                    break
                }
            }
        }
    }

    private fun String.toInstruction2(): Instruction2 = when (val maskMatch = maskRegex.matchEntire(this)) {
        null -> {
            val assignMatch = assignRegex.matchEntire(this)
            require(assignMatch != null) { "Invalid instruction: $this" }
            Assign2(
                address = assignMatch.groupValues[1].toLong(),
                value = assignMatch.groupValues[2].toLong()
            )
        }
        else -> SetMask2(maskMatch.groupValues[1])
    }
}
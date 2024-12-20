package org.clechasseur.adventofcode2019

class IntcodeComputer(program: List<Long>, vararg initialInput: Long, initialState: State = State.RUNNING) {
    companion object {
        enum class State {
            RUNNING, WAITING_FOR_INPUT, DONE
        }

        private const val addOp = 1
        private const val timesOp = 2
        private const val saveOp = 3
        private const val outOp = 4
        private const val jumpIfTrueOp = 5
        private const val jumpIfFalseOp = 6
        private const val lessThanOp = 7
        private const val equalsOp = 8
        private const val offsetRelativeBaseOp = 9
        private const val endOp = 99

        private const val positionMode = 0
        private const val immediateMode = 1
        private const val relativeMode = 2
    }

    private var state = initialState
    private val ram = program.toMutableList()
    private val input = initialInput.toMutableList()
    private val output = mutableListOf<Long>()
    private var ip = 0
    private val modes = mutableListOf<Int>()
    private var relativeBase = 0
    private val ops = mapOf(
            addOp to Add(),
            timesOp to Times(),
            saveOp to Save(),
            outOp to Out(),
            jumpIfTrueOp to JumpIfTrue(),
            jumpIfFalseOp to JumpIfFalse(),
            lessThanOp to LessThan(),
            equalsOp to Equals(),
            offsetRelativeBaseOp to OffsetRelativeBase(),
            endOp to End()
    )

    init {
        run()
    }

    val memory: List<Long>
        get() = ram

    val done: Boolean
        get() = state == State.DONE

    fun addInput(vararg values: Long) {
        input.addAll(values.toList())
        if (state == State.WAITING_FOR_INPUT) {
            state = State.RUNNING
            run()
        }
    }

    fun addAsciiInput(s: String) {
        addInput(*s.map { it.toLong() }.toLongArray())
    }

    fun hasOutput() = output.isNotEmpty()

    fun readOutput(): Long {
        require(hasOutput()) { "No output to read" }
        return output.removeAt(0)
    }

    fun readAllOutput(): List<Long> {
        val allOutput = output.toList()
        output.clear()
        return allOutput
    }

    fun printAsciiOutput() {
        readAllOutput().forEach { print(it.toChar()) }
    }

    fun snapshot(): IntcodeComputer {
        require(state != State.RUNNING) { "Cannot snapshot a running computer" }
        val clone = IntcodeComputer(ram, *input.toLongArray(), initialState = state)
        clone.output.addAll(output)
        clone.ip = ip
        clone.relativeBase = relativeBase
        return clone
    }

    private fun run() {
        while (state == State.RUNNING) {
            nextOp().execute()
        }
    }

    private fun getRam(offset: Int): Long = when {
        offset < 0 -> error("Negative memory address access: $offset")
        offset in ram.indices -> ram[offset]
        else -> 0
    }

    private fun setRam(offset: Int, value: Long) {
        require(offset >= 0) { "Negative memory address access: $offset" }
        if (offset !in ram.indices) {
            ram.addAll(generateSequence { 0L }.take(offset - ram.indices.last))
        }
        ram[offset] = value
    }

    private fun nextOp(): Op {
        var opVal = getRam(ip++).toInt()
        val opcode = opVal % 100
        val op = ops[opcode] ?: error("Wrong opcode: $opcode")
        opVal /= 100
        modes.clear()
        while (opVal != 0) {
            modes.add(opVal % 10)
            opVal /= 10
        }
        modes.reverse()
        return op
    }

    private fun nextParam(): Long {
        val param = getRam(ip++)
        return when (val mode = nextMode()) {
            positionMode -> getRam(param.toInt())
            immediateMode -> param
            relativeMode -> getRam(param.toInt() + relativeBase)
            else -> error("Wrong parameter mode: $mode")
        }
    }

    private fun save(value: Long) {
        val addr = getRam(ip++)
        when (val mode = nextMode()) {
            positionMode -> setRam(addr.toInt(), value)
            relativeMode -> setRam(addr.toInt() + relativeBase, value)
            else -> error("Wrong parameter mode for saving: $mode")
        }
    }

    private fun nextMode(): Int = when (modes.isEmpty()) {
        true -> 0
        false -> modes.removeAt(modes.size - 1)
    }

    private interface Op {
        fun execute()
    }

    private inner class Add : Op {
        override fun execute() {
            save(nextParam() + nextParam())
        }
    }

    private inner class Times : Op {
        override fun execute() {
            save(nextParam() * nextParam())
        }
    }

    private inner class Save : Op {
        override fun execute() {
            if (input.isNotEmpty()) {
                save(input.removeAt(0))
            } else {
                ip--
                state = State.WAITING_FOR_INPUT
            }
        }
    }

    private inner class Out : Op {
        override fun execute() {
            output.add(nextParam())
        }
    }

    private inner class JumpIfTrue : Op {
        override fun execute() {
            val test = nextParam()
            val move = nextParam()
            if (test != 0L) {
                ip = move.toInt()
            }
        }
    }

    private inner class JumpIfFalse : Op {
        override fun execute() {
            val test = nextParam()
            val move = nextParam()
            if (test == 0L) {
                ip = move.toInt()
            }
        }
    }

    private inner class LessThan : Op {
        override fun execute() {
            val in1 = nextParam()
            val in2 = nextParam()
            save(when (in1 < in2) {
                true -> 1
                false -> 0
            })
        }
    }

    private inner class Equals : Op {
        override fun execute() {
            save(when (nextParam() == nextParam()) {
                true -> 1
                false -> 0
            })
        }
    }

    private inner class OffsetRelativeBase : Op {
        override fun execute() {
            relativeBase += nextParam().toInt()
        }
    }

    private inner class End : Op {
        override fun execute() {
            state = State.DONE
        }
    }
}

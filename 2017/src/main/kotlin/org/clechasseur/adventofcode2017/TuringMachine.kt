package org.clechasseur.adventofcode2017

class TuringMachine(programText: String) {
    private val tape = Tape()
    private val program = programText.toProgram()

    init {
        (0 until program.checksumAfter).forEach { _ ->
            val state = program.states.find { it.name == program.curState } ?: error("Undefined state: ${program.curState}")
            program.curState = tape.apply(state)
        }
    }

    val checksum: Int
        get() = tape.checksum

    private enum class Direction(val diff: Int) {
        LEFT(-1),
        RIGHT(1)
    }

    private data class Action(val value: Int, val direction: Direction, val nextState: String)

    private data class State(val name: String, val actionFor0: Action, val actionFor1: Action)

    private class Tape {
        private val ones = mutableSetOf<Int>()
        private var pos = 0

        val checksum: Int
            get() = ones.size

        fun apply(state: State): String = if (ones.contains(pos)) {
            apply(state.actionFor1)
        } else {
            apply(state.actionFor0)
        }

        private fun apply(action: Action): String {
            if (action.value == 1) {
                ones.add(pos)
            } else {
                ones.remove(pos)
            }
            pos += action.direction.diff
            return action.nextState
        }
    }

    private class Program(val states: List<State>, var curState: String, val checksumAfter: Int)

    private fun String.toProgram(): Program {
        val lineIt = lineSequence().iterator()
        val initialState = lineIt.next().getSingleGroupValue("Begin in state ([a-zA-Z]+).")
        val checksumAfter = lineIt.next().getSingleGroupValue("Perform a diagnostic checksum after (\\d+) steps.").toInt()
        val states = mutableListOf<State>()
        while (lineIt.hasNext()) {
            lineIt.next()
            val name = lineIt.next().getSingleGroupValue("In state ([a-zA-Z]+):")
            lineIt.next()
            val valueFor0 = lineIt.next().getSingleGroupValue("\\s+- Write the value (\\d).").toInt()
            val directionFor0 = Direction.valueOf(
                    lineIt.next().getSingleGroupValue("\\s+- Move one slot to the ([a-zA-Z]+).").toUpperCase())
            val nextStateFor0 = lineIt.next().getSingleGroupValue("\\s+- Continue with state ([a-zA-Z]+).")
            lineIt.next()
            val valueFor1 = lineIt.next().getSingleGroupValue("\\s+- Write the value (\\d).").toInt()
            val directionFor1 = Direction.valueOf(
                    lineIt.next().getSingleGroupValue("\\s+- Move one slot to the ([a-zA-Z]+).").toUpperCase())
            val nextStateFor1 = lineIt.next().getSingleGroupValue("\\s+- Continue with state ([a-zA-Z]+).")
            states.add(State(
                    name = name,
                    actionFor0 = Action(valueFor0, directionFor0, nextStateFor0),
                    actionFor1 = Action(valueFor1, directionFor1, nextStateFor1)
            ))
        }
        return Program(states, initialState, checksumAfter)
    }

    private fun String.getSingleGroupValue(regex: String): String
            = (regex.toRegex().matchEntire(this) ?: error("Invalid format: $this")).groupValues[1]
}
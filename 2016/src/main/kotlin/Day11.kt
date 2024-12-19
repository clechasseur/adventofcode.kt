object Day11 {
    private val input = IsolatedArea(listOf(
        // <REDACTED>
    ), elevatorFloorIdx = 0)

    fun part1(): Int = countSteps(input)

    fun part2(): Int = countSteps(
        IsolatedArea(listOf(
            Floor(input.floors[0].objects + setOf(
                InterestingObject("elerium", ObjectType.GENERATOR),
                InterestingObject("elerium", ObjectType.MICROCHIP),
                InterestingObject("dilithium", ObjectType.GENERATOR),
                InterestingObject("dilithium", ObjectType.MICROCHIP)
            )),
            input.floors[1],
            input.floors[2],
            input.floors[3]
        ), elevatorFloorIdx = input.elevatorFloorIdx)
    )

    private fun countSteps(initialState: IsolatedArea): Int {
        var steps = 0
        var states = setOf(initialState)
        var seenStates = emptySet<Int>()
        while (states.none { it.finished }) {
            states = states.asSequence().flatMap { nextSteps(it) }.filterNot { it.hashCode() in seenStates }.toSet()
            seenStates = seenStates + states.map { it.hashCode() }
            require(states.isNotEmpty()) { "No way to get to the final state" }
            steps++
        }
        return steps
    }

    private fun nextSteps(state: IsolatedArea): Sequence<IsolatedArea> = sequence {
        val curFloorIdx = state.elevatorFloorIdx
        when (curFloorIdx) {
            state.floors.indices.first -> listOf(state.floors.indices.first + 1)
            state.floors.indices.last -> listOf(state.floors.indices.last - 1)
            else -> listOf(curFloorIdx - 1, curFloorIdx + 1)
        }.forEach { destFloorIdx ->
            state.floors[curFloorIdx].objects.forEach { obj ->
                val newFloors = state.floors.toMutableList()
                newFloors[curFloorIdx] = Floor(newFloors[curFloorIdx].objects - obj)
                newFloors[destFloorIdx] = Floor(newFloors[destFloorIdx].objects + obj)
                val newState = IsolatedArea(newFloors, destFloorIdx)
                if (newState.valid) {
                    yield(newState)
                }
            }
            state.floors[curFloorIdx].objects.forEachIndexed { idx1, obj1 ->
                state.floors[curFloorIdx].objects.drop(idx1 + 1).forEach { obj2 ->
                    val newFloors = state.floors.toMutableList()
                    newFloors[curFloorIdx] = Floor(newFloors[curFloorIdx].objects - setOf(obj1, obj2))
                    newFloors[destFloorIdx] = Floor(newFloors[destFloorIdx].objects + setOf(obj1, obj2))
                    val newState = IsolatedArea(newFloors, destFloorIdx)
                    if (newState.valid) {
                        yield(newState)
                    }
                }
            }
        }
    }

    private enum class ObjectType(val id: String) {
        GENERATOR("G"),
        MICROCHIP("M"),
    }

    private data class InterestingObject(val element: String, val type: ObjectType) {
        override fun toString(): String = "${element[0].toUpperCase()}${element[1]}${type.id}"
    }

    private data class Floor(val objects: Set<InterestingObject>) {
        val valid: Boolean
            get() = !(objects.any { it.type == ObjectType.GENERATOR } && objects.any { microchip ->
                microchip.type == ObjectType.MICROCHIP && objects.none { generator ->
                    generator.type == ObjectType.GENERATOR && generator.element == microchip.element
                }
            })

        override fun toString(): String = "[${objects.joinToString(",")}]"
    }

    private data class IsolatedArea(val floors: List<Floor>, val elevatorFloorIdx: Int) {
        init {
            require(floors.size == 4) { "Isolated area needs to have 4 floors" }
            require(elevatorFloorIdx in floors.indices) { "Elevator needs to be on a valid floor" }
        }

        val valid: Boolean
            get() = floors.all { it.valid }

        val finished: Boolean
            get() = floors.dropLast(1).all { it.objects.isEmpty() } && elevatorFloorIdx == floors.indices.last

        override fun toString(): String = "{[" + floors.mapIndexed { i, f ->
            "$i${if (i == elevatorFloorIdx) "*" else ""}: $f"
        }.joinToString(",") + "]}"
    }
}
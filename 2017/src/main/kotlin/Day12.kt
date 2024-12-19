object Day12 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val inputRegex = Regex("""^(\d+) <-> (\d+(?:, \d+)*)$""")

    fun part1(): Int {
        val graph = input.lineSequence().map { it.toProgram() }.map { it.id to it }.toMap()
        val connectedTo0 = connectedPrograms(graph, 0)
        return connectedTo0.size
    }

    fun part2(): Int {
        val graph = input.lineSequence().map { it.toProgram() }.map { it.id to it }.toMap()
        val seenIds = mutableSetOf<Program>()
        var nextId = graph.values.firstOrNull { !seenIds.contains(it) }?.id
        var numGroups = 0
        while (nextId != null) {
            seenIds.addAll(connectedPrograms(graph, nextId))
            numGroups++
            nextId = graph.values.firstOrNull { !seenIds.contains(it) }?.id
        }
        return numGroups
    }

    private fun connectedPrograms(graph: Map<Int, Program>, fromId: Int): Set<Program> = mutableSetOf<Program>().apply {
        visit(graph, this, fromId)
    }

    private fun visit(graph: Map<Int, Program>, visited: MutableSet<Program>, fromId: Int) {
        val program = graph[fromId] ?: error("Wrong program id: $fromId")
        if (!visited.contains(program)) {
            visited.add(program)
            program.pipes.forEach { id -> visit(graph, visited, id) }
        }
    }

    private data class Program(val id: Int) {
        var pipes = emptyList<Int>()
    }

    private fun String.toProgram(): Program {
        val match = inputRegex.matchEntire(this) ?: error("Wrong program format: $this")
        val program = Program(match.groupValues[1].toInt())
        program.pipes = match.groupValues[2].split(", ").map { it.toInt() }
        return program
    }
}

object Day7 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val programRegex = Regex("""^([a-z]+) \((\d+)\)(?: -> ([a-z]+(?:, [a-z]+)*))?$""")
    private val programs = input.lineSequence().map { it.toProgram() }.toList()

    fun part1() = ProgramTree(programs).root.program.name

    fun part2(): Int {
        val tree = ProgramTree(programs)
        val imbalanced = tree.findImbalanced()!!
        val targetWeight = imbalanced.parent!!.childs.first { it.weight != imbalanced.weight }.weight
        return targetWeight - (imbalanced.weight - imbalanced.program.weight)
    }

    private data class Program(val name: String, val weight: Int, val childs: List<String>)

    private fun String.toProgram(): Program {
        val match = programRegex.matchEntire(this)
        require(match != null) { "Non-matching program string" }
        var childs: List<String> = emptyList()
        if (match.groupValues.indices.last >= 3 && match.groupValues[3].isNotEmpty()) {
            childs = match.groupValues[3].split(", ")
        }
        return Program(match.groupValues[1], match.groupValues[2].toInt(), childs)
    }

    private class ProgramNode(val program: Program) {
        var parent: ProgramNode? = null
        val childs = mutableListOf<ProgramNode>()

        val weight: Int by lazy {
            program.weight + childs.map { it.weight }.sum()
        }

        fun findImbalanced(): ProgramNode? {
            val childByWeight = mutableMapOf<Int, MutableList<ProgramNode>>()
            childs.forEach { childNode ->
                childByWeight.getOrPut(childNode.weight, { mutableListOf() }).add(childNode)
            }
            if (childByWeight.size <= 1) {
                return null
            }
            return childByWeight.toList().find { it.second.size == 1 }!!.second.single()
        }
    }

    private class ProgramTree(programs: List<Program>) {
        val root: ProgramNode

        init {
            val nodes = programs.map { it.name to ProgramNode(it) }.toMap().toMutableMap()
            programs.forEach { program ->
                val parent = nodes[program.name]!!
                program.childs.forEach { childName ->
                    val child = nodes[childName]!!
                    child.parent = parent
                    parent.childs.add(child)
                }
            }
            var node = nodes.values.first()
            while (node.parent != null) {
                node = node.parent!!
            }
            root = node
        }

        fun findImbalanced(): ProgramNode? = findImbalancedIn(root)

        private fun findImbalancedIn(node: ProgramNode): ProgramNode? {
            val imbalanced = node.childs.asSequence().mapNotNull { findImbalancedIn(it) }.firstOrNull()
            return imbalanced ?: node.findImbalanced()
        }
    }
}

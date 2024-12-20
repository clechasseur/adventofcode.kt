import org.clechasseur.adventofcode2020.Day6Data

object Day6 {
    private val input = Day6Data.input

    fun part1(): Int = input.toGroups().sumBy { it.yes.size }

    fun part2(): Int = input.toGroupsWithAll().sumBy { it.yes.size }

    private class Group(val yes: Set<Char>)

    private fun String.toGroups(): List<Group> {
        val groups = mutableListOf<Group>()
        val yes = mutableSetOf<Char>()
        lineSequence().forEach { line -> when {
            line.isEmpty() -> {
                groups.add(Group(yes.toSet()))
                yes.clear()
            }
            else -> line.forEach { yes.add(it) }
        } }
        if (yes.isNotEmpty()) {
            groups.add(Group(yes.toSet()))
        }
        return groups
    }

    private fun String.toGroupsWithAll(): List<Group> {
        val groups = mutableListOf<Group>()
        val people = mutableListOf<String>()
        lineSequence().forEach { line -> when {
            line.isEmpty() -> {
                groups.add(people.toGroupWithAll())
                people.clear()
            }
            else -> people.add(line)
        } }
        if (people.isNotEmpty()) {
            groups.add(people.toGroupWithAll())
        }
        return groups
    }

    private fun List<String>.toGroupWithAll(): Group {
        val yes = mutableSetOf<Char>()
        ('a'..'z').forEach { letter ->
            if (all { it.contains(letter) }) {
                yes.add(letter)
            }
        }
        return Group(yes)
    }
}
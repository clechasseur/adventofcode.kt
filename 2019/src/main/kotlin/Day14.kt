import kotlin.math.ceil
import kotlin.math.min

object Day14 {
    private val input = """
        <REDACTED>
    """.trimIndent().lineSequence().map { it.toReaction() }.toList()

    fun part1(): Long = getOreFor(Component(1L, "FUEL"), mutableListOf())

    fun part2(): Long {
        val oreForSoMuchFuel = getOreFor(Component(3_000_000L, "FUEL"), mutableListOf())
        return (3_000_000L * 1_000_000_000_000L) / oreForSoMuchFuel
    }

    private fun getOreFor(component: Component, excess: MutableList<Component>): Long = when (component.name) {
        "ORE" -> component.quantity
        else -> {
            when (val available = excess.getExcess(component)) {
                component.quantity -> 0L
                else -> {
                    val required = component.quantity - available
                    val reaction = input.find { it.output.name == component.name }!!.producingAtLeast(required)
                    val oreForReaction = reaction.inputs.map { getOreFor(it, excess) }.sum()
                    excess.addExcess(Component(reaction.output.quantity - required, component.name))
                    oreForReaction
                }
            }
        }
    }

    private fun MutableList<Component>.getExcess(component: Component): Long {
        val reserve = find { it.name == component.name } ?: return 0L
        remove(reserve)
        val excess = min(reserve.quantity, component.quantity)
        if (reserve.quantity - excess > 0L) {
            add(Component(reserve.quantity - excess, reserve.name))
        }
        return excess
    }

    private fun MutableList<Component>.addExcess(component: Component) {
        val reserve = find { it.name == component.name }
        val excess = component.quantity + (reserve?.quantity ?: 0L)
        if (reserve != null) {
            remove(reserve)
        }
        add(Component(excess, component.name))
    }
}

private data class Component(val quantity: Long, val name: String)

private data class Reaction(val inputs: List<Component>, val output: Component)

private fun String.toComponent(): Component {
    val (quantity, name) = split(' ')
    return Component(quantity.toLong(), name)
}

private fun String.toReaction(): Reaction {
    val (inputs, output) = split(" => ")
    return Reaction(inputs.split(", ").map { it.toComponent() }, output.toComponent())
}

private operator fun Component.times(n: Long) = Component(quantity * n, name)
private operator fun Reaction.times(n: Long) = Reaction(inputs.map { it * n }, output * n)
private fun Reaction.producingAtLeast(quantity: Long)
        = this * ceil(quantity.toDouble() / output.quantity.toDouble()).toLong()

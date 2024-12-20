import org.clechasseur.adventofcode2019.Direction
import org.clechasseur.adventofcode2019.IntcodeComputer
import org.clechasseur.adventofcode2019.Pt
import org.clechasseur.adventofcode2019.move
import kotlin.math.min

object Day11 {
    private val input = listOf<Long>() // <REDACTED>

    private const val black = 0
    private const val white = 1
    private val colorsToString = mapOf(
            black to ' ',
            white to '#'
    )

    fun part1() = runRobotRoutine().size

    fun part2(): String {
        val finalPanels = runRobotRoutine(Pt(0, 0) to white).moveToPositive()
        val maxX = finalPanels.map { it.key.x }.max()!!
        val maxY = finalPanels.map { it.key.y }.max()!!
        return (maxY downTo 0).joinToString("\n") { y ->
            (0..maxX).map { x -> colorsToString[finalPanels.getOrDefault(Pt(x, y), black)] }.joinToString("")
        }
    }

    private fun runRobotRoutine(vararg initialPanels: Pair<Pt, Int>): Map<Pt, Int> {
        val panels = initialPanels.toMap().toMutableMap()
        val computer = IntcodeComputer(input)
        var robot = Pt(0, 0)
        var direction = Direction.UP
        while (!computer.done) {
            computer.addInput(panels.getOrDefault(robot, black).toLong())
            val output = computer.readAllOutput()
            if (output.isNotEmpty()) {
                require(output.size == 2) { "Wrong number of output: ${output.size}" }
                panels[robot] = output[0].toInt()
                require(panels[robot] in black..white) { "Wrong panel color: ${panels[robot]}" }
                direction = when (output[1]) {
                    0L -> direction.left
                    1L -> direction.right
                    else -> error("Wrong direction output: ${output[1]}")
                }
                robot = robot.move(direction)
            }
        }
        return panels
    }
}

private fun Map<Pt, Int>.moveToPositive(): Map<Pt, Int> {
    val minX = min(map { it.key.x }.min()!!, 0)
    val minY = min(map { it.key.y }.min()!!, 0)
    return map { Pt(it.key.x - minX, it.key.y - minY) to it.value }.toMap()
}

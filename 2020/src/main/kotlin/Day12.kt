import org.clechasseur.adventofcode2020.Direction
import org.clechasseur.adventofcode2020.Pt
import org.clechasseur.adventofcode2020.manhattan

object Day12 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    fun part1(): Int {
        var pos = Pt.ZERO
        var facing = Direction.RIGHT
        input.lineSequence().forEach { op ->
            var move = op.substring(1).toInt()
            when (op[0]) {
                'N' -> pos += (Direction.UP.displacement * move)
                'S' -> pos += (Direction.DOWN.displacement * move)
                'E' -> pos += (Direction.RIGHT.displacement * move)
                'W' -> pos += (Direction.LEFT.displacement * move)
                'L' -> {
                    while (move > 0) {
                        facing = facing.left
                        move -= 90
                    }
                }
                'R' -> {
                    while (move > 0) {
                        facing = facing.right
                        move -= 90
                    }
                }
                'F' -> pos += (facing.displacement * move)
                else -> error("Wrong opcode: ${op[0]}")
            }
        }
        return manhattan(pos, Pt.ZERO)
    }

    fun part2(): Int {
        var pos = Pt.ZERO
        var speed = Pt(10, -1)
        input.lineSequence().forEach { op ->
            var move = op.substring(1).toInt()
            when (op[0]) {
                'N' -> speed += (Direction.UP.displacement * move)
                'S' -> speed += (Direction.DOWN.displacement * move)
                'E' -> speed += (Direction.RIGHT.displacement * move)
                'W' -> speed += (Direction.LEFT.displacement * move)
                'L' -> {
                    while (move > 0) {
                        speed = Pt(speed.y, -speed.x)
                        move -= 90
                    }
                }
                'R' -> {
                    while (move > 0) {
                        speed = Pt(-speed.y, speed.x)
                        move -= 90
                    }
                }
                'F' -> pos += (speed * move)
                else -> error("Wrong opcode: ${op[0]}")
            }
        }
        return manhattan(pos, Pt.ZERO)
    }
}
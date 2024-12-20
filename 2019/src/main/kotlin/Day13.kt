import org.clechasseur.adventofcode2019.Day13Data
import org.clechasseur.adventofcode2019.IntcodeComputer
import org.clechasseur.adventofcode2019.Pt
import kotlin.math.sign

object Day13 {
    private val input = Day13Data.input

    private const val empty = 0L
    private const val wall = 1L
    private const val block = 2L
    private const val paddle = 3L
    private const val ball = 4L

    private const val neutral = 0L
    private const val tiltedLeft = -1L
    private const val tiltedRight = 1L

    fun part1() = IntcodeComputer(input).readScreen().filter { it.value == block }.count()

    fun part2(): Long {
        val computer = IntcodeComputer(listOf(2L) + input.drop(1))
        var screen = computer.readScreen()
        var paddlePos = screen.singleTile(paddle)
        var ballPos = screen.singleTile(ball)
        var ballSpeed = 0
        while (!computer.done) {
            //render(screen)
            if (ballPos.x != paddlePos.x || ballPos.y != paddlePos.y - 1) {
                val nextBallX = ballPos.x + ballSpeed
                computer.addInput((nextBallX - paddlePos.x).sign.toLong())
            } else {
                computer.addInput(neutral)
            }
            screen = screen + computer.readScreen()
            val newBallPos = screen.singleTile(ball)
            ballSpeed = (newBallPos.x - ballPos.x).sign
            paddlePos = screen.singleTile(paddle)
            ballPos = newBallPos
        }
        return screen[Pt(-1, 0)] ?: error("No score found")
    }

    private fun IntcodeComputer.readScreen(): Map<Pt, Long> = readAllOutput().chunked(3).map {
        (x, y, tileId) -> Pt(x.toInt(), y.toInt()) to tileId
    }.toMap()

    private fun Map<Pt, Long>.singleTile(id: Long) = asSequence().filter { it.value == id }.single().key

    private fun render(screen: Map<Pt, Long>) {
        val maxX = screen.map { it.key.x }.max()!!
        val maxY = screen.map { it.key.y }.max()!!
        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                val tileChar = when (val tileId = screen[Pt(x, y)]) {
                    null, empty -> ' '
                    wall -> '#'
                    block -> 'W'
                    paddle -> '_'
                    ball -> 'o'
                    else -> error("Wrong tile id: $tileId")
                }
                print(tileChar)
            }
            println()
        }
        generateSequence(0) { when (it) {
            9 -> 0
            else -> it + 1
        } }.take(maxX + 1).forEach { print(it) }
        println()
        println()
    }
}

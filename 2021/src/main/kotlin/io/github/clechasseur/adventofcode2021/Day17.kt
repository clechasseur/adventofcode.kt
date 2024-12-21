package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.util.Pt
import kotlin.math.max

object Day17 {
    private val targetArea = object {
        val x = 0..0 // <REDACTED>
        val y = 0..0 // <REDACTED>
    }

    fun part1(): Int {
        val xVelocity = findXVelocity()
        val highY = (10..600).mapNotNull { highestY(Pt(xVelocity, it)) }.maxByOrNull { it }
        require(highY != null) { "No dice" }
        return highY
    }

    fun part2(): Int = (-300..600).flatMap { yVelocity ->
        (1..300).mapNotNull { xVelocity ->
            highestY(Pt(xVelocity, yVelocity))
        }
    }.count()

    private fun `fire!`(initialVelocity: Pt): Sequence<Pt> = sequence {
        var pt = Pt.ZERO
        yield(pt)

        var velocity = initialVelocity
        while (true) {
            pt += velocity
            val drift = Pt(when {
                velocity.x > 0 -> -1
                velocity.x < 0 -> 1
                else -> 0
            }, -1)
            velocity += drift
            yield(pt)
        }
    }

    private fun highestY(velocity: Pt): Int? {
        val it = `fire!`(velocity).iterator()
        var pt = it.next()
        var high = pt.y
        while ((pt.x !in targetArea.x || pt.y !in targetArea.y) && pt.y >= targetArea.y.first) {
            pt = it.next()
            high = max(high, pt.y)
        }
        return if (pt.x in targetArea.x && pt.y in targetArea.y) high else null
    }

    private fun findXVelocity(): Int {
        var xVelocity = 0
        var x = 0
        while (x !in targetArea.x) {
            x += xVelocity
            xVelocity += 1
        }
        return xVelocity
    }
}

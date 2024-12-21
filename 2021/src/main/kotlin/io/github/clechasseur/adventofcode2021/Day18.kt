package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day18Data
import kotlin.math.ceil
import kotlin.math.floor

object Day18 {
    private val data = Day18Data.data

    fun part1(): Int = data.lines().map { it.iterator().nextNode() }.reduce { acc, n -> acc + n }.magnitude

    fun part2(): Int {
        val nodes = data.lines().map { it.iterator().nextNode() }
        return nodes.indices.flatMap { i ->
            nodes.indices.filter { it != i }.map { j ->
                nodes[i] + nodes[j]
            }
        }.maxOf { it.magnitude }
    }

    private class Explosion(var leftParticle: Int?, var rightParticle: Int?)

    private class Node(var value: Int?, var left: Node?, var right: Node?) {
        val isLeaf: Boolean
            get() = value != null

        val magnitude: Int
            get() = value ?: (3 * left!!.magnitude + 2 * right!!.magnitude)

        fun clone(): Node = if (isLeaf) makeLeaf(value!!) else makePair(left!!.clone(), right!!.clone())

        fun reduce(): Node {
            val node = clone()
            while (true) {
                if (node.explode(0) == null) {
                    if (!node.split()) {
                        break
                    }
                }
            }
            return node
        }

        private fun smash(particle: Int, goLeft: Boolean) {
            if (isLeaf) {
                value = value!! + particle
            } else if (goLeft) {
                left!!.smash(particle, true)
            } else {
                right!!.smash(particle, false)
            }
        }

        private fun explode(depth: Int): Explosion? = if (isLeaf) null else when (depth) {
            4 -> {
                val explosion = Explosion(left!!.magnitude, right!!.magnitude)
                left = null
                right = null
                value = 0
                explosion
            }
            else -> {
                var explosion = left!!.explode(depth + 1)
                if (explosion != null) {
                    if (explosion.rightParticle != null) {
                        right!!.smash(explosion.rightParticle!!, true)
                        explosion.rightParticle = null
                    }
                } else {
                    explosion = right!!.explode(depth + 1)
                    if (explosion?.leftParticle != null) {
                        left!!.smash(explosion.leftParticle!!, false)
                        explosion.leftParticle = null
                    }
                }
                explosion
            }
        }

        private fun split(): Boolean = if (isLeaf) {
            if (value!! > 9) {
                left = makeLeaf(floor(value!!.toDouble() / 2).toInt())
                right = makeLeaf(ceil(value!!.toDouble() / 2).toInt())
                value = null
                true
            } else {
                false
            }
        } else left!!.split() || right!!.split()

        override fun toString(): String = if (isLeaf) value!!.toString() else "[${left!!},${right!!}]"
    }

    private fun makeLeaf(value: Int) = Node(value, null, null)
    private fun makePair(left: Node, right: Node) = Node(null, left, right)

    private operator fun Node.plus(right: Node): Node = makePair(this, right).reduce()

    private fun Iterator<Char>.nextNode(): Node = when (val c = next()) {
        '[' -> nextPair()
        else -> makeLeaf(c.toString().toInt())
    }

    private fun Iterator<Char>.nextPair(): Node {
        val left = nextNode()
        val comma = next()
        require(comma == ',') { "Invalid pair sequence, expected comma" }
        val right = nextNode()
        val closing = next()
        require(closing == ']') { "Invalid pair sequence, expected closing bracket" }
        return makePair(left, right)
    }
}

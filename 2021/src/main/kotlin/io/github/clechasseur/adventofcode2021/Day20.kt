package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day20Data
import io.github.clechasseur.adventofcode2021.util.Pt

object Day20 {
    private val algoData = Day20Data.algoData
    private val imageData = Day20Data.imageData

    fun part1(): Int = imageData.toImage().`enhance!`().`enhance!`().lit

    fun part2(): Int = generateSequence(imageData.toImage()) { it.`enhance!`() }.drop(50).first().lit

    private object Algo {
        fun `enhance!`(pixels: List<Boolean>): Boolean {
            require(pixels.size == 9) { "Need 9 pixels to enhance, got ${pixels.size}" }
            return algoData[pixels.joinToString("") { if (it) "1" else "0" }.toInt(2)] == '#'
        }
    }

    private class Image(val data: Map<Pt, Boolean>, val unknown: Boolean) {
        val lit: Int
            get() {
                require(!unknown) { "Cannot compute lit pixels in canvas with infinite lit pixels" }
                return data.values.count { it }
            }

        operator fun get(pt: Pt): Boolean = data.getOrDefault(pt, unknown)

        fun pixelsForEnhancement(pt: Pt): List<Boolean> = (-1..1).flatMap { yd ->
            (-1..1).map { xd -> this[pt + Pt(xd, yd)] }
        }

        fun `enhance!`(): Image {
            val minx = data.keys.minOf { it.x } - 1
            val miny = data.keys.minOf { it.y } - 1
            val maxx = data.keys.maxOf { it.x } + 1
            val maxy = data.keys.maxOf { it.y } + 1
            val newData = (miny..maxy).flatMap { y ->
                (minx..maxx).map { x -> Pt(x, y) to Algo.`enhance!`(pixelsForEnhancement(Pt(x, y))) }
            }.toMap()
            val newUnknown = Algo.`enhance!`(List(9) { unknown })
            return Image(newData, newUnknown)
        }
    }

    private fun String.toImage() = Image(lines().flatMapIndexed { y, line ->
        line.mapIndexed { x, c -> Pt(x, y) to (c == '#') }
    }.toMap(), false)
}

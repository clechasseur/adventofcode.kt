package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.util.Direction
import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.y2022.data.Day8Data

object Day8 {
    private val input = Day8Data.input

    fun part1(): Int {
        val grid = input.toGrid()
        return (0 until grid.height).flatMap { y ->
            (0 until grid.width).map { x -> Pt(x, y) }
        }.count { grid.visible(it) }
    }

    fun part2(): Int {
        val grid = input.toGrid()
        return (0 until grid.height).flatMap { y ->
            (0 until grid.width).map { x -> Pt(x, y) }
        }.maxOf { grid.viewingDistance(it) }
    }

    private class Grid(val trees: List<List<Int>>) {
        val height: Int
            get() = trees.size
        val width: Int
            get() = trees.first().size

        fun validTree(tree: Pt): Boolean = (tree.x in 0  until width) && (tree.y in 0 until height)
        fun treeHeight(pt: Pt): Int = trees[pt.y][pt.x]

        fun visible(tree: Pt, direction: Direction): Boolean = generateSequence(tree) {
            it + direction.displacement
        }.drop(1).takeWhile { validTree(it) }.all { treeHeight(it) < treeHeight(tree) }

        fun visible(tree: Pt): Boolean = Direction.values().any { visible(tree, it) }

        fun viewingDistance(tree: Pt, direction: Direction): Int = generateSequence(tree) {
            it + direction.displacement
        }.takeWhile {
            validTree(it)
        }.map {
            treeHeight(it)
        }.withIndex().windowed(2, 1).takeWhile { iHeights ->
            iHeights.first().index == 0 || iHeights[0].value < treeHeight(tree)
        }.count()

        fun viewingDistance(tree: Pt): Int = Direction.values().fold(1) { acc, d ->
            acc * viewingDistance(tree, d)
        }
    }

    private fun String.toGrid(): Grid = Grid(lines().map { line ->
        line.map { tree -> tree.toString().toInt() }
    })
}

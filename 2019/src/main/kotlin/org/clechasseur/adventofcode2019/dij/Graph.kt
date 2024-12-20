package org.clechasseur.adventofcode2019.dij

interface Graph<T : Comparable<T>> {
    fun allPassable(): List<T>
    fun neighbours(node: T): List<T>
    fun dist(a: T, b: T): Long
    fun isABetter(a: T, b: T): Boolean = false
}

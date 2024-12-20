package org.clechasseur.adventofcode2020

fun <T> Iterable<Iterable<T>>.pivot(): List<List<T>> = sequence {
    val iterators = map { it.iterator() }
    while (iterators.all { it.hasNext() }) {
        yield(iterators.map { it.next() })
    }
}.toList()

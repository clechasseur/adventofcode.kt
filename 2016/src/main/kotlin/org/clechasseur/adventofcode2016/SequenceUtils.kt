package org.clechasseur.adventofcode2016

fun <T> Iterable<Iterable<T>>.pivot(): List<List<T>> = sequence {
    val iterators = map { it.iterator() }
    while (iterators.all { it.hasNext() }) {
        yield(iterators.map { it.next() })
    }
}.toList()

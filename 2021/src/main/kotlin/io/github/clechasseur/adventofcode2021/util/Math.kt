package io.github.clechasseur.adventofcode2021.util

fun <T> permutations(elements: List<T>): List<List<T>> {
    val all = mutableListOf<List<T>>()
    permutationsInPlace(elements.toMutableList(), 0, all)
    return all
}

fun <T> permutationsInPlace(elements: MutableList<T>, k: Int, all: MutableList<List<T>>) {
    for (i in k until elements.size) {
        swap(elements, i, k)
        permutationsInPlace(elements, k + 1, all)
        swap(elements, k, i)
    }
    if (k == elements.size - 1) {
        all.add(elements.toList())
    }
}

fun <T> swap(elements: MutableList<T>, i: Int, j: Int) {
    val e = elements[i]
    elements[i] = elements[j]
    elements[j] = e
}

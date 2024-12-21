package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day13Data

object Day13 {
    private val input = Day13Data.input

    fun part1(): Int = input.toPairs().withIndex().filter { (_, pair) ->
        pair.first <= pair.second
    }.sumOf { it.index + 1 }

    fun part2(): Int {
        val dividerPacket1 = ListElement(listOf(ListElement(listOf(IntElement(2)))))
        val dividerPacket2 = ListElement(listOf(ListElement(listOf(IntElement(6)))))
        val packets = (input.toPairs().flatMap {
            listOf(it.first, it.second)
        } + dividerPacket1 + dividerPacket2).sorted()
        val index1 = packets.indexOf(dividerPacket1) + 1
        val index2 = packets.indexOf(dividerPacket2) + 1
        return index1 * index2
    }

    private sealed interface Element : Comparable<Element>

    private data class IntElement(val value: Int) : Element {
        override fun compareTo(other: Element): Int = when (other) {
            is IntElement -> value.compareTo(other.value)
            is ListElement -> ListElement(listOf(this)).compareTo(other)
        }
    }

    private data class ListElement(val elements: List<Element>) : Element {
        override fun compareTo(other: Element): Int = when (other) {
            is IntElement -> compareTo(ListElement(listOf(other)))
            is ListElement -> {
                val cmp = elements.zip(other.elements).map { (a, b) ->
                    a.compareTo(b)
                }.firstOrNull { it != 0 } ?: 0
                if (cmp != 0) cmp else elements.size.compareTo(other.elements.size)
            }
        }
    }

    private fun String.toPairs(): List<Pair<Element, Element>> = lineSequence().filter {
        it.isNotBlank()
    }.chunked(2).map { (a, b) ->
        a.toElement() to b.toElement()
    }.toList()

    private fun String.toElement(): Element = when (first()) {
        '[' -> substring(1 until length - 1).toListElement()
        else -> toIntElement()
    }

    private fun String.toIntElement(): Element = IntElement(toInt())

    private fun String.toListElement(): Element {
        var i = 0
        val elements = mutableListOf<Element>()
        while (i in indices) {
            if (this[i] == '[') {
                var j = i + 1
                var depth = 1
                while (depth > 0) {
                    require(j in indices) { "Mismatched list at position $i in $this" }
                    if (this[j] == '[') {
                        depth++
                    } else if (this[j] == ']') {
                        depth--
                    }
                    j++
                }
                elements.add(substring(i until j).toElement())
                i = j + 1
            } else {
                var j = i + 1
                while (j in indices && this[j] != ',') {
                    j++
                }
                elements.add(substring(i until j).toElement())
                i = j + 1
            }
        }
        return ListElement(elements)
    }
}

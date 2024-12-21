package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day20Data

object Day20 {
    private val input = Day20Data.input

    private const val decryptionKey = 0L // <REDACTED>

    fun part1(): Long = findGrove(input.lines().map { it.toLong() }, 1)

    fun part2(): Long = findGrove(input.lines().map { it.toLong() * decryptionKey }, 10)

    private fun findGrove(file: List<Long>, mixTimes: Int): Long {
        val llfile = LinkedList(file.withIndex())
        (0 until mixTimes).forEach { _ ->
            file.indices.forEach { i ->
                val node = llfile.findNode { it.index == i }!!
                if (node.value.value >= 0L) {
                    node.moveForward(node.value.value % (file.size - 1).toLong())
                } else {
                    node.moveBackward(-node.value.value % (file.size - 1).toLong())
                }
            }
        }

        val node0 = llfile.findNode { it.value == 0L }!!
        val after1000 = node0.skipForward(1000L)
        val after2000 = after1000.skipForward(1000L)
        val after3000 = after2000.skipForward(1000L)
        return after1000.value.value + after2000.value.value + after3000.value.value
    }

    private class Node<T>(val value: T) {
        var next: Node<T>? = null
        var prev: Node<T>? = null

        fun moveForward(n: Long) {
            (0 until n).forEach { _ ->
                val nextNext = next!!.next
                nextNext!!.prev = this
                prev!!.next = next
                next!!.prev = prev
                next!!.next = this
                prev = next
                next = nextNext
            }
        }
        fun moveBackward(n: Long) {
            (0 until n).forEach { _ ->
                val prevPrev = prev!!.prev
                prevPrev!!.next = this
                next!!.prev = prev
                prev!!.prev = this
                prev!!.next = next
                next = prev
                prev = prevPrev
            }
        }

        fun skipForward(n: Long): Node<T> {
            var node = this
            (0 until n).forEach { _ ->
                node = node.next!!
            }
            return node
        }
    }

    private class LinkedList<T>(items: Iterable<T> = emptyList()) {
        init {
            items.forEach { add(it) }
        }

        var head: Node<T>? = null
            private set

        fun add(value: T) {
            val node = Node(value)
            node.next = head ?: node
            node.prev = head?.prev ?: node
            head?.prev?.next = node
            head?.prev = node
            head = head ?: node
        }

        fun findNode(pred: (T) -> Boolean): Node<T>? = if (head == null) {
            null
        } else if (pred(head!!.value)) {
            head
        } else {
            var node = head!!.next
            while (node != head && !pred(node!!.value)) {
                node = node.next
            }
            if (node != head) node else null
        }
    }
}

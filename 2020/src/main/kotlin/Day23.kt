object Day23 {
    private const val input = "<REDACTED>"

    fun part1(): String {
        val state = State(input.map { it.toString().toLong() })
        state.moveMany(100)
        return state.label
    }

    fun part2(): Long {
        val state = State(oneMillionCups())
        state.moveMany(10_000_000)
        return state.hiddenStars
    }

    private fun oneMillionCups(): List<Long> = input.map { it.toString().toLong() } + (10L..1_000_000L)

    private class State(val cups: LinkedList<Long>) {
        constructor(cups: Iterable<Long>) : this(LinkedList(cups))

        val nodeMap = cups.nodeMap
        val maxCup: Long = cups.maxBy { it.value }!!.value

        val label: String
            get() = nodeMap[1L]!!.drop(1).joinToString("")

        val hiddenStars: Long
            get() {
                val oneCup = nodeMap[1L]!!
                return oneCup.next!!.value * oneCup.next!!.next!!.value
            }

        fun move() {
            val currentNode = cups.head!!
            val pickedUp = cups.pickUpNext(currentNode, 3)
            var destinationCup = if (currentNode.value == 1L) {
                maxCup
            } else {
                currentNode.value - 1L
            }
            while (pickedUp.find { it.value == destinationCup } != null) {
                destinationCup--
                if (destinationCup == 0L) {
                    destinationCup = maxCup
                }
            }
            val destinationNode = nodeMap[destinationCup]!!
            cups.insertAfter(destinationNode, pickedUp)
            cups.rotateTo(currentNode.next!!)
        }
        fun moveMany(n: Int) {
            repeat(n) { move() }
        }
    }

    private class Node<T>(val value: T) : Iterable<Node<T>> {
        var next: Node<T>? = null

        override fun iterator(): Iterator<Node<T>> = object : Iterator<Node<T>> {
            private var node = this@Node
            private var done = false

            override fun hasNext(): Boolean = !done
            override fun next(): Node<T> {
                val next = node
                node = node.next!!
                done = node == this@Node
                return next
            }
        }

        override fun toString(): String = value.toString()
    }

    private class LinkedList<T>(elements: Iterable<T> = emptyList()) : Iterable<Node<T>> {
        var head: Node<T>? = null
            private set

        init {
            val first = Node(elements.first())
            first.next = first
            var last = first
            elements.asSequence().drop(1).forEach { element ->
                val node = Node(element)
                last.next = node
                node.next = first
                last = node
            }
            head = first
        }

        val nodeMap: Map<T, Node<T>>
            get() = associateBy { it.value }

        fun pickUpNext(node: Node<T>, n: Int): Node<T> {
            val pickedUp = node.next!!
            val lastPickedUp = pickedUp.elementAt(n - 1)
            val after = lastPickedUp.next!!
            lastPickedUp.next = pickedUp
            if (head in pickedUp) {
                head = after
            }
            node.next = after
            return pickedUp
        }

        fun insertAfter(node: Node<T>, newNode: Node<T>) {
            newNode.last().next = node.next
            node.next = newNode
        }

        fun rotateTo(newHead: Node<T>) {
            head = newHead
        }

        override fun iterator(): Iterator<Node<T>> = head?.iterator() ?: EmptyIterator()

        override fun toString(): String = "[${joinToString()}]"

        private class EmptyIterator<T> : Iterator<Node<T>> {
            override fun hasNext(): Boolean = false
            override fun next(): Node<T> {
                error("No elements")
            }
        }
    }
}

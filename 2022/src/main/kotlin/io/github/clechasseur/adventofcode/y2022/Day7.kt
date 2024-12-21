package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day7Data

object Day7 {
    private val input = Day7Data.input

    private val shellRegex = """^\$ (cd|ls)(?: ([a-zA-Z./]+))?$""".toRegex()
    private val fileRegex = """^(\d+|dir) (.+)$""".toRegex()

    fun part1(): Long = input.toFilesystem().flat().filter { it.size <= 100_000L }.sumOf { it.size }

    fun part2(): Long {
        val fs = input.toFilesystem()
        val avail = 70_000_000L - fs.size
        val needed = 30_000_000L - avail
        return fs.flat().filter { it.size >= needed }.minOf { it.size }
    }

    private data class File(val name: String, val size: Long)

    private class Dir(val name: String) {
        val files = mutableListOf<File>()

        val dirs = mutableListOf<Dir>()

        var parent: Dir? = null

        val size: Long
            get() = files.sumOf { it.size } + dirs.sumOf { it.size }

        fun flat(): Sequence<Dir> = sequenceOf(this) + dirs.asSequence().flatMap { it.flat() }
    }

    private fun String.toFilesystem(): Dir {
        val root = Dir("/")
        var pwd = root
        var ls = false
        for (line in lines()) {
            val shellMatch = shellRegex.matchEntire(line)
            if (shellMatch != null) {
                when (shellMatch.groupValues[1]) {
                    "cd" -> {
                        ls = false
                        pwd = when (val dirName = shellMatch.groupValues[2]) {
                            "/" -> root
                            ".." -> pwd.parent ?: error("..: unknown directory")
                            else -> pwd.dirs.singleOrNull { it.name == dirName } ?: error("$dirName: not found")
                        }
                    }
                    "ls" -> ls = true
                    else -> error("${shellMatch.groupValues[1]}: unknown command")
                }
            } else {
                require(ls) { "Invalid command in command mode: $line" }
                val fileMatch = fileRegex.matchEntire(line) ?: error("Invalid file in ls mode: $line")
                val (size, name) = fileMatch.destructured
                when (size) {
                    "dir" -> {
                        val subdir = Dir(name)
                        subdir.parent = pwd
                        pwd.dirs.add(subdir)
                    }
                    else -> pwd.files.add(File(name, size.toLong()))
                }
            }
        }
        return root
    }
}

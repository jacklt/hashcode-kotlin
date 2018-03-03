package com.cose

import java.io.File

class Parser(inputFile: File) {
    val lines = inputFile.readLines()
    var currentLine = -1

    fun next() = lines[++currentLine]

    fun next(count: Int) = lines.subList(currentLine + 1, currentLine + 1 + count).apply { currentLine += count }
}

private operator fun <E> List<E>.component6(): E = this[5]

fun parse(inputFile: File) = with(Parser(inputFile)) {

    val (r, c, f, n, b, t) = next().split(" ").map { it.toInt() }
    val rides = next(r).map {
        val (a, b, x, y, start, finish) = it.split(" ").map { it.toInt() }
        Ride(a, b, x, y, start, finish)
    }
    Problem(r, c, f, n, b, t, rides)
}

fun write(outputFile: File, solution: Solution) = outputFile.writeText(
    solution.cars.map {
        "${it.rideIds.size} ${it.rideIds.joinToString(" ")}"
    }
        .joinToString("\n")
)
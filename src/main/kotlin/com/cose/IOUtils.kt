package com.cose

import java.io.File

class Parser(inputFile: File) {
    val lines = inputFile.readLines()
    var currentLine = -1

    fun next() = lines[++currentLine]

    fun next(count: Int) = lines.subList(currentLine + 1, currentLine + 1 + count).apply { currentLine += count }
}


fun parse(inputFile: File) = with(Parser(inputFile)) {
    val (r, c, l, h) = next().split(" ").map { it.toInt() }
    val pizza = next(r.toInt())
    Problem(r, c, l, h, pizza.map { it.map { it == 'M' } })
}

fun write(outputFile: File, solution: Solution) = outputFile.writeText("""${solution.slices.size}
${solution.slices.map { "${it.p1.first} ${it.p1.second} ${it.p2.first} ${it.p2.second}" }.joinToString("\n")}""")
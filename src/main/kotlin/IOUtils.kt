package com.github.jacklt.hashcode

import java.io.File

class Parser(inputFile: File) {
    val lines = inputFile.readLines()
    var currentLine = -1

    fun next() = lines[++currentLine]

    fun next(count: Int) = lines.subList(currentLine + 1, currentLine + 1 + count).apply { currentLine += count }
}

fun parse(inputFile: File) = with(Parser(inputFile)) {
    val r = next().toInt()
    val slides = next(r)
    Problem(slides.mapIndexed { i, s ->
        val row = s.split(" ")
        Photo(i, row[0] == "H", row.drop(2))
    })
}

fun write(outputFile: File, solution: Solution) = outputFile.writeText("""${solution.sideshow.size}
${solution.sideshow.map { it.ids.joinToString(" ") }.joinToString("\n")}""")

fun List<Int>.checkAllDistinct(name: String) = apply {
    if (size != distinct().size) error("$name: $size != ${distinct().size}")
}

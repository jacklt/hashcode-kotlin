package com.cose

import java.io.File

fun main(args: Array<String>) {
    val fn = listOf("example", "small", "medium", "big")
    fn.take(1).forEach {
        App.solveForX(inputFile = File("io/$it.in"), outputFile = File("io/$it.out"))
    }
}

data class Problem(
        val rows: Int,  // R (1≤R≤1000)  isthenumberofrows,
        val columns: Int,  // C (1 ≤ C ≤ 1000) is the number of columns,
        val minIng: Int,  // L (1 ≤ L ≤ 1000)  is the minimum number of each ingredient cells in a slice,
        val maxSliceSize: Int,  // H (1 ≤ H ≤ 1000)  is the maximum total number of cells of a slice
        val mashrooms: List<List<Boolean>>
)

data class Slice(val p1: Pair<Int, Int>, val p2: Pair<Int, Int>)
data class Solution(val slices: List<Slice>)

object App {
    fun solveForX(inputFile: File, outputFile: File) {
        val problem = parse(inputFile)
        println("Problem: $problem")

        // do some stuff

        val solution = Solution(listOf(
                Slice(0 to 0, 2 to 1),
                Slice(0 to 2, 2 to 2),
                Slice(0 to 3, 2 to 4)
        ))
        println("Solution: $solution")
        write(outputFile, solution)
    }
}

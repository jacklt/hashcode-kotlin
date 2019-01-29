package com.github.jacklt.hashcode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.measureTimeMillis

const val DEBUG = false
const val VISUALIZE = false

fun main() {
    measureTimeMillis {
        runBlocking {
            val fn = listOf("example", "small", "medium", "big")
            fn.slice(0..3).forEach {
                App.solveForX(inputFile = File("io/$it.in"), outputFile = File("io/$it.out")).join()
            }
        }
    }.also { println("Completed in ${it}ms") }
}

data class Problem(
    val rows: Int,  // R (1≤R≤1000)  isthenumberofrows,
    val columns: Int,  // C (1 ≤ C ≤ 1000) is the number of columns,
    val minIng: Int,  // L (1 ≤ L ≤ 1000)  is the minimum number of each ingredient cells in a slice,
    val maxSliceSize: Int,  // H (1 ≤ H ≤ 1000)  is the maximum total number of cells of a slice
    val mashrooms: List<List<Boolean>>
)

data class Slice(var x1: Int, var y1: Int, var x2: Int, var y2: Int)
data class Solution(val slices: List<Slice>)

object App : CoroutineScope {
    val job = Job()
    override val coroutineContext = job

    fun solveForX(inputFile: File, outputFile: File) = launch {
        val problem = parse(inputFile)
        if (DEBUG) println("Problem: $problem")

        // do some stuff

        val solution = async {
            Solution(listOf(
                Slice(0, 0, 2, 1),
                Slice(0, 2, 2, 2),
                Slice(0, 3, 2, 4)
            ))
        }

        if (DEBUG) println("Solution: ${solution.await()}()")
        // write(outputFile, solution)
    }
}

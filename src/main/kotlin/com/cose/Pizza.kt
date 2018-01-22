package com.cose

import java.io.File
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    measureTimeMillis {
        val fn = listOf("example", "small", "medium", "big")
        fn.take(4).forEach {
            App.solveForX(inputFile = File("io/$it.in"), outputFile = File("io/$it.out"))
        }
    }.also { println("Completed in ${it}ms") }
}

data class Problem(
        val rows: Int,  // R (1≤R≤1000)  isthenumberofrows,
        val columns: Int,  // C (1 ≤ C ≤ 1000) is the number of columns,
        val minIng: Int,  // L (1 ≤ L ≤ 1000)  is the minimum number of each ingredient cells in a slice,
        val maxSliceSize: Int,  // H (1 ≤ H ≤ 1000)  is the maximum total number of cells of a slice
        val mashrooms: List<List<Boolean>>
) {
    val ingredients = mashrooms.flatMap { it }
    val flatSize = ingredients.size
    val mashroomCount = ingredients.count { it }
    val tomatoCount = flatSize - mashroomCount
}

data class Slice(val x1: Int, val y1: Int, val x2: Int, val y2: Int, val mashCount: Int, val tomatoCount: Int)
data class Solution(val slices: List<Slice>)

object App {
    fun solveForX(inputFile: File, outputFile: File) {
        val problem = parse(inputFile)

        val slicePair = with(problem) {
            val minSize = minIng * 2
            (1..maxSliceSize).flatMap { row ->
                (1..maxSliceSize).mapNotNull { col ->
                    val size = row * col
                    if (size in (minSize..maxSliceSize)) {
                        (row to col)
                    } else null
                }
            }
        }.sortedBy { it.first * it.second }.also { println("STATS: slicePair: $it") }

        // Completed in 151ms


        val validSlice = with(problem) {
            mashrooms.mapIndexed { x, row ->
                row.mapIndexed { y, item ->
                    slicePair.mapNotNull { (nRow, nCol) ->
                        val endX = x + nRow - 1
                        val endY = y + nCol - 1
                        if (endX < mashrooms.size && endY < row.size) {
                            val sliceIngredients = mashrooms.slice(x..endX).flatMap {
                                it.slice(y..endY)
                            }
                            val mashCount = sliceIngredients.count { it }
                            val tomatoCount = sliceIngredients.size - mashCount
                            if (mashCount >= minIng && (tomatoCount >= minIng)) {
                                Slice(x, y, endX, endY, mashCount, tomatoCount)
                            } else null
                        } else null
                    }
                }
            }
        } // .also { println("STATS: validSlice: $it") }

        // Completed in 5824ms

        val flatValidSlice = validSlice.flatMap { it }.flatMap { it }
        println("STATS: ${flatValidSlice.size} valid slice")

        // println("\nslice:\n" + validSlice.map { it.map { it.size }.joinToString("") }.joinToString("\n"))

//        val hitMap = problem.mashrooms.map { it.map { 0 }.toMutableList() }
//        flatValidSlice.forEach {
//            (it.x1 until it.x2).forEach { i ->
//                (it.y1 until it.y2).forEach { j ->
//                    hitMap[i][j] += 1
//                }
//            }
//        }
//        println("\nhit:\n" + hitMap.map { it.joinToString(" ") }.joinToString("\n"))

       // println("\nvisualize:\n" + problem.mashrooms.map { it.map { if (it) 'x' else 'o' }.joinToString(" ") }.joinToString("\n"))

        var pizzaAvailable = problem.flatSize
        var mashAvailable = problem.mashroomCount
        var tomatoAvailable = problem.tomatoCount
        val availableMap = problem.mashrooms.map { it.map { false }.toMutableList() }
        val bestSlices = mutableListOf<Slice>()
        validSlice.forEachIndexed { x, row ->
            val maxSliceLeft = pizzaAvailable / problem.maxSliceSize.toDouble() / problem.minIng
            val maxMash = Math.max(problem.minIng.toDouble(), mashAvailable / maxSliceLeft)
            row.forEachIndexed { y, slices ->
                slices.lastOrNull { slice ->
                    slice.mashCount <= maxMash && availableMap.slice(slice.x1..slice.x2).flatMap {
                        it.slice(slice.y1..slice.y2)
                    }.all { !it }.also {
                        if (it) {
                            (slice.x1..slice.x2).forEach { x ->
                                (slice.y1..slice.y2).forEach { y ->
                                    availableMap[x][y] = true
                                }
                            }
                        }
                    }
                }?.also {
                            bestSlices.add(it)
                        }
            }
        }

        println("STATS: Found: ${bestSlices.size}")

        val points = availableMap.map { it.count { it } }.sum()
        println("STATS: Points: $points / ${problem.flatSize} = ${points / problem.flatSize.toDouble() * 100}%\n")

        // println("\nsol:\n" + availableMap.map { it.map { if (it) 'x' else 'o' }.joinToString("") }.joinToString("\n"))


        // println("Problem: $problem")

        // do some stuff

        val solution = Solution(listOf(
                Slice(0, 0, 2, 1, 0, 0),
                Slice(0, 2, 2, 2, 0, 0),
                Slice(0, 3, 2, 4, 0, 0)
        ))

        // println("Solution: $solution")
        // write(outputFile, solution)
    }
}

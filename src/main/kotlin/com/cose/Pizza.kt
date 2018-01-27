package com.cose

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import kotlin.math.ceil
import kotlin.system.measureTimeMillis

const val DEBUG = false
const val VISUALIZE = false

fun main(args: Array<String>) {
    measureTimeMillis {
        val fn = listOf("example", "small", "medium", "big")
        fn.slice(0..3).forEach {
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
    val ingredients = mashrooms.flatten()
    val flatSize = ingredients.size
    val mashroomCount = ingredients.count { it }
    val tomatoCount = flatSize - mashroomCount
}

data class Slice(var x1: Int, var y1: Int, var x2: Int, var y2: Int, val mashCount: Int, val tomatoCount: Int)
data class Solution(val slices: List<Slice>)

object App {
    fun solveForX(inputFile: File, outputFile: File) = runBlocking {
        val problem = parse(inputFile)

        val slicePair = problem.findSlicePair()
        println("slicePair: $slicePair")

        val validSlice = problem.filterValidSlice(slicePair)
        if (DEBUG) println("validSlice: $validSlice")

        val flatValidSlice = validSlice.flatten().flatten()
        println("validSlice count: ${flatValidSlice.size}")

        val validSliceX1Y2 = flatValidSlice.groupBy { it.x1 }.map {
            it.value.groupBy { it.y2 }.map {
                it.value.sortedBy { it.mashCount + it.tomatoCount }
            }.sortedByDescending { it[0].y2 }
        }.sortedBy { it[0][0].x1 }
        val validSliceX2Y1 = flatValidSlice.groupBy { it.x2 }.map {
            it.value.groupBy { it.y1 }.map {
                it.value.sortedBy { it.mashCount + it.tomatoCount }
            }.sortedBy { it[0].y1 }
        }.sortedByDescending { it[0][0].x2 }
        val validSliceX2Y2 = flatValidSlice.groupBy { it.x2 }.map {
            it.value.groupBy { it.y2 }.map {
                it.value.sortedBy { it.mashCount + it.tomatoCount }
            }.sortedByDescending { it[0].y2 }
        }.sortedByDescending { it[0][0].x2 }

        val (availableMap, bestSlices) = listOf(validSlice, validSliceX1Y2, validSliceX2Y1, validSliceX2Y2).flatMap { slices ->
            listOf(1.0, 1.1, 2.0).map {
                async { problem.solve(slices, it) }// .apply { println("$it -> ${first.calcPoints()}") }
            }.map { it.await() }
        }.maxBy { (availableMap, _) ->
                    availableMap.calcPoints()
                }!!

        problem.visualize(bestSlices)

        println("Solution! slice count: ${bestSlices.size}")

        val points = availableMap.calcPoints()
        println("Points: $points / ${problem.flatSize} = ${points / problem.flatSize.toDouble() * 100}%\n")

        write(outputFile, Solution(bestSlices))
    }

    private fun List<MutableList<Boolean>>.calcPoints() = map { it.count { !it } }.sum()

    private fun Problem.solve(validSlice: List<List<List<Slice>>>, magicNumber: Double): Pair<List<MutableList<Boolean>>, MutableList<Slice>> {
        var pizzaAvailable = flatSize.toDouble()
        var mashAvailable = mashroomCount.toDouble()
        var tomatoAvailable = tomatoCount.toDouble()
        val availableMap = mashrooms.map { it.map { true }.toMutableList() }
        val bestSlices = mutableListOf<Slice>()

        validSlice.forEachIndexed { x, row ->
            val maxSliceLeft = ceil(pizzaAvailable / maxSliceSize.toDouble()) // problem.minIng
            val maxMash = minIng + ceil((mashAvailable - maxSliceLeft * minIng) / maxSliceLeft * magicNumber)
            val maxTomato = minIng + ceil((tomatoAvailable - maxSliceLeft * minIng) / maxSliceLeft * magicNumber)
            row.forEachIndexed { y, slices ->
                slices.lastOrNull { slice ->
                    slice.mashCount <= maxMash && slice.tomatoCount <= maxTomato && availableMap.slice(slice.x1..slice.x2).flatMap {
                        it.slice(slice.y1..slice.y2)
                    }.all { it }
                }?.also {
                            // println("$pizzaAvailable $maxSliceLeft ($mashAvailable ${it.mashCount} $maxMash) ($tomatoAvailable $tomatoAvailable $maxTomato)")
                            (it.x1..it.x2).forEach { x ->
                                (it.y1..it.y2).forEach { y ->
                                    availableMap[x][y] = false
                                }
                            }
                            pizzaAvailable -= it.mashCount + it.tomatoCount
                            mashAvailable -= it.mashCount
                            tomatoAvailable -= it.tomatoCount
                            bestSlices.add(it)
                        }
            }
        }

        // try expand
        bestSlices.forEach { slice ->
            while (expandTop(slice, availableMap)) {
            }
            while (expandBottom(slice, availableMap)) {
            }
            while (expandLeft(slice, availableMap)) {
            }
            while (expandRight(slice, availableMap)) {
            }
        }
        return Pair(availableMap, bestSlices)
    }

    private fun Problem.expandTop(slice: Slice, availableMap: List<MutableList<Boolean>>): Boolean {
        var expanded = false
        val prevRow = slice.x1 - 1
        if (prevRow >= 0) {
            val colRange = slice.y1..slice.y2
            if (colRange.all { availableMap[prevRow][it] } && maxSliceSize >= (slice.x2 - slice.x1 + 1) * (slice.y2 - slice.y1 + 1) + colRange.count()) {
                colRange.forEach { availableMap[prevRow][it] = false }
                slice.x1 -= 1
                expanded = true
            }
        }
        return expanded
    }

    private fun Problem.expandBottom(slice: Slice, availableMap: List<MutableList<Boolean>>): Boolean {
        var expanded = false
        val nextRow = slice.x2 + 1
        if (nextRow < rows) {
            val colRange = slice.y1..slice.y2
            if (colRange.all { availableMap[nextRow][it] } && maxSliceSize > (slice.x2 - slice.x1 + 1) * (slice.y2 - slice.y1 + 1) + colRange.count()) {
                colRange.forEach { availableMap[nextRow][it] = false }
                slice.x2 += 1
                expanded = true
            }
        }
        return expanded
    }

    private fun Problem.expandLeft(slice: Slice, availableMap: List<MutableList<Boolean>>): Boolean {
        var expanded = false
        val prevCol = slice.y1 - 1
        if (prevCol >= 0) {
            val rowRange = slice.x1..slice.x2
            if (rowRange.all { availableMap[it][prevCol] } && maxSliceSize > (slice.x2 - slice.x1 + 1) * (slice.y2 - slice.y1 + 1) + rowRange.count()) {
                rowRange.forEach { availableMap[it][prevCol] = false }
                slice.y1 -= 1
                expanded = true
            }
        }
        return expanded
    }

    private fun Problem.expandRight(slice: Slice, availableMap: List<MutableList<Boolean>>): Boolean {
        var expanded = false
        val nextCol = slice.y2 + 1
        if (nextCol < columns) {
            val rowRange = slice.x1..slice.x2
            if (rowRange.all { availableMap[it][nextCol] } && maxSliceSize > (slice.x2 - slice.x1 + 1) * (slice.y2 - slice.y1 + 1) + rowRange.count()) {
                rowRange.forEach { availableMap[it][nextCol] = false }
                slice.y2 += 1
                expanded = true
            }
        }
        return expanded
    }

    private fun Problem.findSlicePair(): List<Pair<Int, Int>> {
        val minSize = minIng * 2
        return (1..maxSliceSize).flatMap { row ->
            (1..maxSliceSize).mapNotNull { col ->
                val size = row * col
                if (size in (minSize..maxSliceSize)) {
                    (row to col)
                } else null
            }
        }.sortedBy { it.first * it.second }
    }

    private fun Problem.filterValidSlice(slicePair: List<Pair<Int, Int>>): List<List<List<Slice>>> {
        return mashrooms.mapIndexed { x, row ->
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
    }
}

package com.cose

import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import kotlin.system.measureTimeMillis

const val DEBUG = true
const val VISUALIZE = false

fun main(args: Array<String>) {
    measureTimeMillis {
        val fn = listOf("a_example", "b_should_be_easy", "c_no_hurry", "d_metropolis", "e_high_bonus")
        fn.slice(0..0).forEach {
            App.solveForX(inputFile = File("io/$it.in"), outputFile = File("io/$it.out"))
        }
    }.also { println("Completed in ${it}ms") }
}

data class Problem(
    val rows: Int, // R – number of rows of the grid (1 ≤ R ≤ 10000)
    val columns: Int, // C – number of columns of the grid (1 ≤ C ≤ 10000)
    val carsCount: Int, // F – number of vehicles in the fleet (1 ≤ F ≤ 1000)
    val ridesCount: Int, // N – number of rides (1 ≤ N ≤ 10000)
    val bonus: Int, // B – per-ride bonus for starting the ride on time (1 ≤ B ≤ 10000)
    val steps: Int, // T – number of steps in the simulation (1 ≤ T ≤ 10 )
    val rides: List<Ride>
)

data class Ride(
    val x1: Int, // a – the row of the start intersection (0 ≤ a < R)
    val y1: Int, // b – the column of the start intersection (0 ≤ b < C)
    val x2: Int, // x – the row of the finish intersection (0 ≤ x < R)
    val y2: Int, // y – the column of the finish intersection (0 ≤ y < C)
    val start: Int, // s – the earliest start(0 ≤ s < T)
    val finish: Int // f – the latest finish (0 ≤ f ≤ T) , (f ≥ s + |x − a| + |y − b|)
)

data class Car(val rideIds: List<Int>)
data class Solution(val cars: List<Car>)

object App {
    fun solveForX(inputFile: File, outputFile: File) = runBlocking {
        val problem = parse(inputFile)
        if (DEBUG) println("Problem: $problem")

        // do some stuff

        val solution = Solution(listOf(
            Car(listOf(0)),
            Car(listOf(2, 1))
        ))

        if (DEBUG) println("Solution: $solution")
        write(outputFile, solution)
    }
}

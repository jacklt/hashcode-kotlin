package com.github.jacklt.hashcode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.measureTimeMillis

const val DEBUG = true
const val VISUALIZE = false

fun main() {
    measureTimeMillis {
        runBlocking {
            val fn = listOf("a_example", "b_lovely_landscapes", "c_memorable_moments", "d_pet_pictures", "e_shiny_selfies")
            fn.slice(0..4).forEach {
                println("[Start] $it")
                App.solveForX(inputFile = File("io/$it.in"), outputFile = File("io/$it.out")).join()
                println("[Finish] $it\n")
            }
        }
    }.also { println("Completed in ${it}ms") }
}

data class Photo(val id: Int, val hor: Boolean, val tags: List<String>)

data class Problem(val photos: List<Photo>)

data class Slide(val ids: List<Int>, val tags: List<String>)

data class Solution(val sideshow: List<Slide>) {
    init {
        sideshow.flatMap { it.ids }.checkAllDistinct("slideshow")
    }
}

object App : CoroutineScope {
    val job = Job()
    override val coroutineContext = job

    fun solveForX(inputFile: File, outputFile: File) = launch {
        val problem = parse(inputFile)
        // if (DEBUG) println("Problem: $problem")

        val solution = async {
            val photos = problem.photos.sortedByDescending { it.tags.size }
            val hor = photos.filter { it.hor }.map { Slide(listOf(it.id), it.tags) }

            val maxTags = hor.firstOrNull()?.tags?.size ?: 100

            println("tags: " + photos.flatMap { it.tags }.size +" distinct: " + photos.flatMap { it.tags }.distinct().size)
            println("min: " + photos.minBy { it.tags.size }!!.tags.size +" max: " + photos.maxBy { it.tags.size }!!.tags.size)
//
//            val vert = mutableListOf<Slide>()
//
//            val vertPhotos = photos.filter { !it.hor }
//            val vertOther = vertPhotos.reversed().toMutableList()
//            vertPhotos.forEachIndexed { i, p1 ->
//                if (p1 in vertOther) {
//                    if (i % 100 == 0) println("c1 $i")
//                    var currentPair: Photo? = null
//                    var p = 0
//                    vertOther.take(200).forEach { p2 ->
//                        val tags = (p1.tags + p2.tags).distinct()
//                        if (tags.size > p) {
//                            currentPair = p2
//                            p = tags.size
//                            if (tags.size >= maxTags) return@forEach
//                        }
//                    }
//                    if (currentPair != null) {
//                        val p2 = currentPair!!
//                        vertOther.remove(p1)
//                        vertOther.remove(p2)
//                        vert.add(Slide(listOf(p1.id, p2.id), (p1.tags + p2.tags).distinct()))
//                    }
//                }
//            }
//            vertOther.windowed(2, 2).forEach { (p1, p2) ->
//                vert.add(Slide(listOf(p1.id, p2.id), (p1.tags + p2.tags).distinct()))
//            }
//
//            val preSlides = (hor + vert).sortedByDescending { it.tags.size }
            val slides = mutableListOf<Slide>()
//            val other = preSlides.toMutableList()
//            preSlides.forEachIndexed { i, s1 ->
//                if (s1 in other) {
//                    if (i % 200 == 0) println("c2 $i")
//                    val maxP = s1.tags.size / 2
//                    var currentPair: Slide? = null
//                    var p = 0
//                    other.take(100).forEach { s2 ->
//                        val commonSize = s1.tags.filter { it in s2.tags }.size
//                        if (commonSize > p) {
//                            val newP = min(min(commonSize, s1.tags.size - commonSize), s2.tags.size - commonSize)
//                            if (newP > p) {
//                                currentPair = s2
//                                p = newP
//                                if (p == maxP) return@forEach
//                            }
//                        }
//                    }
//                    if (currentPair != null) {
//                        val s2 = currentPair!!
//                        other.remove(s1)
//                        other.remove(s2)
//                        slides.add(s1)
//                        slides.add(s2)
//                    }
//                }
//            }
//             slides.addAll(other)
            Solution(slides)
        }

        write(outputFile, solution.await())
    }
}

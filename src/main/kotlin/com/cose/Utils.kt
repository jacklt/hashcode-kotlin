package com.cose

fun Problem.visualize(bestSlices: MutableList<Slice>) {
    if (VISUALIZE) {
        val sliceMap = mashrooms.map { it.map { 0 }.toMutableList() }
        bestSlices.forEachIndexed { id, it ->
            (it.x1..it.x2).forEach { i ->
                (it.y1..it.y2).forEach { j ->
                    sliceMap[i][j] = 1 + id % 9
                }
            }
        }

        println("""
visualize:
${mashrooms.map { it.map { if (it) 'x' else '.' }.joinToString("") }.joinToString("\n")}

sliceMap:
${sliceMap.map { it.joinToString("") }.joinToString("\n")}
""")
    }
}
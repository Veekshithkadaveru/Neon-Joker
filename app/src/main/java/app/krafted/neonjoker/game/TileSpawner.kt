package app.krafted.neonjoker.game

import kotlin.random.Random

class TileSpawner(private val random: Random = Random.Default) {

    fun hasEmptyCell(grid: Grid): Boolean = grid.values.any { it == 0 }

    fun spawn(grid: Grid): Grid {
        val source = grid.values
        val emptyIndices = ArrayList<Int>(source.size)
        for (i in source.indices) if (source[i] == 0) emptyIndices.add(i)
        if (emptyIndices.isEmpty()) return grid

        val chosen = emptyIndices[random.nextInt(emptyIndices.size)]
        val tier = if (random.nextInt(100) < 10) 2 else 1
        val next = source.copyOf()
        next[chosen] = tier
        return Grid(next)
    }
}

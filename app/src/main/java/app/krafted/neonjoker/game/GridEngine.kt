package app.krafted.neonjoker.game

import app.krafted.neonjoker.game.Grid.Companion.CELLS
import app.krafted.neonjoker.game.Grid.Companion.MAX_TIER
import app.krafted.neonjoker.game.Grid.Companion.SIZE

object GridEngine {

    fun slide(grid: Grid, direction: Direction): SlideResult {
        val source = grid.values
        val working = when (direction) {
            Direction.LEFT -> source.copyOf()
            Direction.RIGHT -> reverseRows(source)
            Direction.UP -> transpose(source)
            Direction.DOWN -> reverseRows(transpose(source))
        }

        var scoreDelta = 0
        val row = IntArray(SIZE)
        for (r in 0 until SIZE) {
            for (c in 0 until SIZE) row[c] = working[r * SIZE + c]
            val (newRow, rowScore) = slideRowLeft(row)
            scoreDelta += rowScore
            for (c in 0 until SIZE) working[r * SIZE + c] = newRow[c]
        }

        val oriented = when (direction) {
            Direction.LEFT -> working
            Direction.RIGHT -> reverseRows(working)
            Direction.UP -> transpose(working)
            Direction.DOWN -> transpose(reverseRows(working))
        }

        val moved = !oriented.contentEquals(source)
        return SlideResult(Grid(oriented), scoreDelta, moved)
    }

    private fun slideRowLeft(row: IntArray): Pair<IntArray, Int> {
        val compacted = IntArray(SIZE)
        var writeIdx = 0
        for (v in row) if (v != 0) compacted[writeIdx++] = v

        val merged = IntArray(SIZE)
        val wasMerge = BooleanArray(SIZE)
        var outIdx = 0
        var scoreDelta = 0
        var i = 0
        while (i < SIZE) {
            val v = compacted[i]
            if (v == 0) {
                i++
                continue
            }
            val canMerge = i + 1 < SIZE &&
                    compacted[i + 1] == v &&
                    v < MAX_TIER &&
                    !wasMerge[outIdx]
            if (canMerge) {
                val newTier = v + 1
                merged[outIdx] = newTier
                wasMerge[outIdx] = true
                scoreDelta += 1 shl newTier
                outIdx++
                i += 2
            } else {
                merged[outIdx] = v
                outIdx++
                i++
            }
        }
        return merged to scoreDelta
    }

    private fun transpose(values: IntArray): IntArray {
        val out = IntArray(CELLS)
        for (r in 0 until SIZE) {
            for (c in 0 until SIZE) {
                out[c * SIZE + r] = values[r * SIZE + c]
            }
        }
        return out
    }

    private fun reverseRows(values: IntArray): IntArray {
        val out = IntArray(CELLS)
        for (r in 0 until SIZE) {
            for (c in 0 until SIZE) {
                out[r * SIZE + (SIZE - 1 - c)] = values[r * SIZE + c]
            }
        }
        return out
    }
}

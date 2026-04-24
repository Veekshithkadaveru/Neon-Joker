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
        val allMoves = mutableListOf<TileMove>()
        val row = IntArray(SIZE)
        for (r in 0 until SIZE) {
            for (c in 0 until SIZE) row[c] = working[r * SIZE + c]
            val rowResult = slideRowLeft(row)
            scoreDelta += rowResult.score
            for (c in 0 until SIZE) working[r * SIZE + c] = rowResult.row[c]

            for (m in rowResult.moves) {
                val fromIdx = toOriginalIndex(r, m.fromCol, direction)
                val toIdx = toOriginalIndex(r, m.toCol, direction)
                val mergedFrom = m.mergedFromCols?.let { (c1, c2) ->
                    toOriginalIndex(r, c1, direction) to toOriginalIndex(r, c2, direction)
                }
                allMoves.add(TileMove(fromIdx, toIdx, m.tier, m.merged, mergedFrom))
            }
        }

        val oriented = when (direction) {
            Direction.LEFT -> working
            Direction.RIGHT -> reverseRows(working)
            Direction.UP -> transpose(working)
            Direction.DOWN -> transpose(reverseRows(working))
        }

        val moved = !oriented.contentEquals(source)
        return SlideResult(Grid(oriented), scoreDelta, moved, allMoves)
    }

    private data class RowTileMove(
        val fromCol: Int,
        val toCol: Int,
        val tier: Int,
        val merged: Boolean,
        val mergedFromCols: Pair<Int, Int>? = null,
    )

    private data class RowSlideResult(
        val row: IntArray,
        val score: Int,
        val moves: List<RowTileMove>,
    )

    private fun slideRowLeft(row: IntArray): RowSlideResult {
        val compacted = IntArray(SIZE)
        val originCol = IntArray(SIZE) { -1 }
        var writeIdx = 0
        for (col in row.indices) {
            if (row[col] != 0) {
                compacted[writeIdx] = row[col]
                originCol[writeIdx] = col
                writeIdx++
            }
        }

        val merged = IntArray(SIZE)
        val moves = mutableListOf<RowTileMove>()
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
                    v < MAX_TIER
            if (canMerge) {
                val newTier = v + 1
                merged[outIdx] = newTier
                scoreDelta += 1 shl newTier
                moves.add(
                    RowTileMove(
                        fromCol = originCol[i],
                        toCol = outIdx,
                        tier = newTier,
                        merged = true,
                        mergedFromCols = originCol[i] to originCol[i + 1],
                    )
                )
                outIdx++
                i += 2
            } else {
                merged[outIdx] = v
                moves.add(
                    RowTileMove(
                        fromCol = originCol[i],
                        toCol = outIdx,
                        tier = v,
                        merged = false,
                    )
                )
                outIdx++
                i++
            }
        }
        return RowSlideResult(merged, scoreDelta, moves)
    }

    private fun toOriginalIndex(workingRow: Int, workingCol: Int, direction: Direction): Int =
        when (direction) {
            Direction.LEFT -> workingRow * SIZE + workingCol
            Direction.RIGHT -> workingRow * SIZE + (SIZE - 1 - workingCol)
            Direction.UP -> workingCol * SIZE + workingRow
            Direction.DOWN -> (SIZE - 1 - workingCol) * SIZE + workingRow
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

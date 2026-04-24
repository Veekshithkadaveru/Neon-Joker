package app.krafted.neonjoker.game

enum class Direction { LEFT, RIGHT, UP, DOWN }

@JvmInline
value class Grid(val values: IntArray) {
    companion object {
        const val SIZE = 4
        const val CELLS = 16
        const val MAX_TIER = 7
        fun empty(): Grid = Grid(IntArray(CELLS))
    }
}

data class TileMove(
    val fromIndex: Int,
    val toIndex: Int,
    val tier: Int,
    val merged: Boolean,
    val mergedFrom: Pair<Int, Int>? = null,
)

data class SlideResult(
    val grid: Grid,
    val scoreDelta: Int,
    val moved: Boolean,
    val moves: List<TileMove> = emptyList(),
)

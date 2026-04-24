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

data class SlideResult(
    val grid: Grid,
    val scoreDelta: Int,
    val moved: Boolean,
)

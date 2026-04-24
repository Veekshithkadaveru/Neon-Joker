package app.krafted.neonjoker.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GridEngineTest {

    private fun gridOf(vararg cells: Int): Grid {
        require(cells.size == Grid.CELLS) { "Grid must be 16 cells, got ${cells.size}" }
        return Grid(cells.copyOf())
    }

    private fun assertGrid(expected: IntArray, actual: Grid) {
        assertTrue(
            "expected ${expected.toList()} but got ${actual.values.toList()}",
            actual.values.contentEquals(expected),
        )
    }

    @Test
    fun leftCompactOnlyNoMerges() {
        val input = gridOf(
            1, 0, 0, 2,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.LEFT)
        assertGrid(
            intArrayOf(
                1, 2, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
            ),
            result.grid,
        )
        assertEquals(0, result.scoreDelta)
        assertTrue(result.moved)
    }

    @Test
    fun leftSingleMerge() {
        val input = gridOf(
            1, 1, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.LEFT)
        assertGrid(
            intArrayOf(
                2, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
            ),
            result.grid,
        )
        assertEquals(4, result.scoreDelta)
        assertTrue(result.moved)
    }

    @Test
    fun leftDoubleMergeInOneRow() {
        val input = gridOf(
            1, 1, 1, 1,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.LEFT)
        assertGrid(
            intArrayOf(
                2, 2, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
            ),
            result.grid,
        )
        assertEquals(8, result.scoreDelta)
        assertTrue(result.moved)
    }

    @Test
    fun leftMergeExhaustion() {
        val input = gridOf(
            1, 1, 2, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.LEFT)
        assertGrid(
            intArrayOf(
                2, 2, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
            ),
            result.grid,
        )
        assertEquals(4, result.scoreDelta)
        assertTrue(result.moved)
    }

    @Test
    fun leftNoChangeReportsMovedFalse() {
        val input = gridOf(
            1, 2, 3, 4,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.LEFT)
        assertGrid(input.values, result.grid)
        assertEquals(0, result.scoreDelta)
        assertFalse(result.moved)
    }

    @Test
    fun tierSevenPairDoesNotMerge() {
        val input = gridOf(
            7, 7, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.LEFT)
        assertGrid(input.values, result.grid)
        assertEquals(0, result.scoreDelta)
        assertFalse(result.moved)
    }

    @Test
    fun rightMergesToRightEdge() {
        val input = gridOf(
            1, 1, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.RIGHT)
        assertGrid(
            intArrayOf(
                0, 0, 0, 2,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
            ),
            result.grid,
        )
        assertEquals(4, result.scoreDelta)
        assertTrue(result.moved)
    }

    @Test
    fun upMergesTopColumn() {
        val input = gridOf(
            1, 0, 0, 0,
            1, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.UP)
        assertGrid(
            intArrayOf(
                2, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
            ),
            result.grid,
        )
        assertEquals(4, result.scoreDelta)
        assertTrue(result.moved)
    }

    @Test
    fun downMergesBottomColumn() {
        val input = gridOf(
            1, 0, 0, 0,
            1, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.DOWN)
        assertGrid(
            intArrayOf(
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                2, 0, 0, 0,
            ),
            result.grid,
        )
        assertEquals(4, result.scoreDelta)
        assertTrue(result.moved)
    }

    @Test
    fun scoreAccumulatesAcrossRows() {
        val input = gridOf(
            1, 1, 1, 1,
            2, 2, 2, 2,
            0, 0, 0, 0,
            0, 0, 0, 0,
        )
        val result = GridEngine.slide(input, Direction.LEFT)
        assertGrid(
            intArrayOf(
                2, 2, 0, 0,
                3, 3, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
            ),
            result.grid,
        )
        assertEquals(24, result.scoreDelta)
        assertTrue(result.moved)
    }
}

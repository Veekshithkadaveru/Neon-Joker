package app.krafted.neonjoker.game

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TileSpawnerTest {

    @Test
    fun spawnOnEmptyBoardPlacesExactlyOneTile() {
        val spawner = TileSpawner(Random(1))
        val result = spawner.spawn(Grid.empty())
        val nonZero = result.values.count { it != 0 }
        val zeros = result.values.count { it == 0 }
        assertEquals(1, nonZero)
        assertEquals(15, zeros)
        val placed = result.values.first { it != 0 }
        assertTrue("expected tier in {1, 2} but got $placed", placed == 1 || placed == 2)
    }

    @Test
    fun spawnOnFullBoardIsNoOp() {
        val full = Grid(IntArray(Grid.CELLS) { 1 })
        val spawner = TileSpawner(Random(1))
        val result = spawner.spawn(full)
        assertTrue(result.values.contentEquals(full.values))
    }

    @Test
    fun spawnDoesNotMutateInputGrid() {
        val input = Grid.empty()
        val snapshot = input.values.copyOf()
        TileSpawner(Random(1)).spawn(input)
        assertTrue(input.values.contentEquals(snapshot))
    }

    @Test
    fun spawnedValuesAreAlwaysOneOrTwo() {
        val spawner = TileSpawner(Random(42))
        repeat(1_000) {
            val result = spawner.spawn(Grid.empty())
            val placed = result.values.first { it != 0 }
            assertTrue("tier $placed not in {1, 2}", placed == 1 || placed == 2)
        }
    }

    @Test
    fun distributionIsApproximately90To10() {
        val spawner = TileSpawner(Random(42))
        var tier1 = 0
        var tier2 = 0
        repeat(10_000) {
            val placed = spawner.spawn(Grid.empty()).values.first { it != 0 }
            if (placed == 1) tier1++ else if (placed == 2) tier2++
        }
        assertTrue("tier1 count $tier1 out of tolerance", tier1 in 8800..9200)
        assertTrue("tier2 count $tier2 out of tolerance", tier2 in 800..1200)
        assertEquals(10_000, tier1 + tier2)
    }

    @Test
    fun hasEmptyCellReflectsGridState() {
        assertTrue(TileSpawner().hasEmptyCell(Grid.empty()))
        val full = Grid(IntArray(Grid.CELLS) { 1 })
        assertFalse(TileSpawner().hasEmptyCell(full))
    }
}

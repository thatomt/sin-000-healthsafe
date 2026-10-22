package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlertLevelStoreTest {

    @Test
    void startsAtZero() {
        assertEquals(0, new AlertLevelStore().get());
    }

    @Test
    void acceptsTheBoundaryValuesZeroAndEight() {
        AlertLevelStore store = new AlertLevelStore();
        store.set(8);
        assertEquals(8, store.get());
        store.set(0);
        assertEquals(0, store.get());
    }

    @Test
    void rejectsValuesOutsideTheRange() {
        AlertLevelStore store = new AlertLevelStore();
        assertThrows(IllegalArgumentException.class, () -> store.set(-1));
        assertThrows(IllegalArgumentException.class, () -> store.set(9));
    }

    @Test
    void aRejectedValueLeavesTheCurrentLevelUnchanged() {
        AlertLevelStore store = new AlertLevelStore();
        store.set(5);
        assertThrows(IllegalArgumentException.class, () -> store.set(99));
        assertEquals(5, store.get());
    }
}
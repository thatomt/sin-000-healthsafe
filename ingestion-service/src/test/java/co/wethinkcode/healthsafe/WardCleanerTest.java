package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WardCleanerTest {

    @Test
    void cleanTrimsAndCollapsesSpaces() {
        assertEquals("South Wing", WardCleaner.clean("  South   Wing "));
    }

    @Test
    void cleanTurnsPlaceholdersAndBlanksIntoNull() {
        for (String placeholder : new String[]{"N/A", "n/a", "TBD", "unknown", "-", "NaN", "", "   "}) {
            assertNull(WardCleaner.clean(placeholder), "should be null: '" + placeholder + "'");
        }
    }

    @Test
    void cleanKeepsNegativeNumbersSoLaterCodeCanFlagThem() {
        assertEquals("-1", WardCleaner.clean("-1"));
    }

    @Test
    void titleCaseFixesMixedCasing() {
        assertEquals("North Wing", WardCleaner.titleCase("north WING"));
    }
}
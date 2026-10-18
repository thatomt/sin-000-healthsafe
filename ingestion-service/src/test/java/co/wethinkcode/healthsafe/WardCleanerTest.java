package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @Test
    void parseBedsAcceptsValidCountsIncludingZero() {
        List<String> notes = new ArrayList<>();
        assertEquals(5, WardCleaner.parseBeds(" 5 ", notes));
        assertEquals(0, WardCleaner.parseBeds("0", notes));
        assertTrue(notes.isEmpty());
    }

    @Test
    void parseBedsFlagsBadValuesWithANote() {
        for (String bad : new String[]{"five", "full", "-1", "2023", "N/A"}) {
            List<String> notes = new ArrayList<>();
            assertNull(WardCleaner.parseBeds(bad, notes), "should be null: " + bad);
            assertEquals(1, notes.size(), "should explain why: " + bad);
        }
    }
}
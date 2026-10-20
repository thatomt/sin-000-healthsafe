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
    @Test
    void cleanRowFixesTheMessyW05Row() {
        Ward ward = WardCleaner.cleanRow(new String[]{"w-05", "east wing ", "PAEDIATRICS", "five"});

        assertEquals("W-05", ward.wardId());
        assertEquals("East Wing", ward.wing());
        assertEquals("Paediatrics", ward.department());
        assertNull(ward.bedsAvailable());
        assertEquals(1, ward.notes().size());
    }

    @Test
    void cleanRowKeepsIcuUppercaseAndMapsPediatrics() {
        assertEquals("ICU", WardCleaner.cleanRow(new String[]{"W-09", "North Wing", "ICU", "N/A"}).department());
        assertEquals("Paediatrics", WardCleaner.cleanRow(new String[]{"W-11", "East Wing", "Pediatrics", "3"}).department());
    }
    @Test
    void cleanAllMergesDuplicateW05KeepingTheValidBedCount() {
        List<String[]> rows = List.of(
                new String[]{"W-05", "East Wing", "Paediatrics", "5"},
                new String[]{"w-05", "east wing ", "PAEDIATRICS", "five"});

        List<Ward> wards = WardCleaner.cleanAll(rows);

        assertEquals(1, wards.size());
        assertEquals(5, wards.get(0).bedsAvailable());
        assertTrue(wards.get(0).notes().stream().anyMatch(n -> n.contains("duplicate")));
    }

    @Test
    void cleanAllNotesConflictingValuesAndKeepsTheFirst() {
        List<String[]> rows = List.of(
                new String[]{"W-20", "East Wing", "Cardiology", "4"},
                new String[]{"W-20", "East Wing", "Cardiology", "9"});

        Ward ward = WardCleaner.cleanAll(rows).get(0);

        assertEquals(4, ward.bedsAvailable());
        assertTrue(ward.notes().stream().anyMatch(n -> n.contains("conflict")));
    }

    @Test
    void cleanAllSkipsBrokenRowsWithoutCrashing() {
        List<String[]> rows = List.of(
                new String[]{},
                new String[]{"W-01", "East Wing", "Cardiology", "3"});

        List<Ward> wards = WardCleaner.cleanAll(rows);

        assertEquals(1, wards.size());
        assertEquals("W-01", wards.get(0).wardId());
    }
}
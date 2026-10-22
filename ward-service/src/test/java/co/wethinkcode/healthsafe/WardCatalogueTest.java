package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WardCatalogueTest {

    private static Ward ward(String id, String department) {
        return new Ward(id, "East Wing", department, 3, List.of());
    }

    private final List<Ward> wards = List.of(
            ward("W-01", "Cardiology"),
            ward("W-05", "Paediatrics"),
            ward("W-06", "Cardiology"),
            ward("W-08", null));

    @Test
    void findByIdIgnoresCaseAndPadding() {
        assertEquals("W-05", WardCatalogue.findById(wards, "  w-05 ").orElseThrow().wardId());
    }

    @Test
    void findByIdReturnsEmptyForUnknownWard() {
        assertTrue(WardCatalogue.findById(wards, "W-99").isEmpty());
        assertTrue(WardCatalogue.findById(wards, null).isEmpty());
    }

    @Test
    void departmentsAreDistinctSortedAndSkipMissing() {
        assertEquals(List.of("Cardiology", "Paediatrics"), WardCatalogue.departments(wards));
    }
}
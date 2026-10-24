package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipmentFailureAlertTest {

    private final Ward ward = new Ward("W-05", "East Wing", "Paediatrics", 5, List.of());

    @Test
    void buildsAnAlertFromTheWardAndTheReport() {
        EquipmentFailureAlert alert = EquipmentFailureAlert.from(ward,
                new EquipmentFailureReport("  ventilator ", " alarm failed "));

        assertEquals("W-05", alert.wardId());
        assertEquals("Paediatrics", alert.department());
        assertEquals("ventilator", alert.equipment());
        assertEquals("alarm failed", alert.description());
        assertFalse(alert.alertId().isBlank());
        assertFalse(alert.reportedAt().isBlank());
    }

    @Test
    void everyAlertGetsItsOwnId() {
        EquipmentFailureReport report = new EquipmentFailureReport("pump", null);
        assertNotEquals(EquipmentFailureAlert.from(ward, report).alertId(),
                EquipmentFailureAlert.from(ward, report).alertId());
    }

    @Test
    void aReportNeedsAnEquipmentName() {
        assertFalse(new EquipmentFailureReport(null, "x").hasEquipment());
        assertFalse(new EquipmentFailureReport("   ", "x").hasEquipment());
        assertTrue(new EquipmentFailureReport("pump", null).hasEquipment());
    }
}
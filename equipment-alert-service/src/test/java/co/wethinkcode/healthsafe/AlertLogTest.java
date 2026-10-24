package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertLogTest {

    private static EquipmentFailureAlert alert(String id) {
        return new EquipmentFailureAlert(id, "W-05", "Paediatrics", "ventilator", "alarm failed", "2026-10-23T10:00:00Z");
    }

    @Test
    void storesANewAlert() {
        AlertLog log = new AlertLog();
        assertTrue(log.add(alert("a1")));
        assertEquals(1, log.all().size());
    }

    @Test
    void ignoresARepeatDeliveryOfTheSameAlert() {
        AlertLog log = new AlertLog();
        log.add(alert("a1"));
        assertFalse(log.add(alert("a1")));
        assertEquals(1, log.all().size());
    }

    @Test
    void keepsAlertsInArrivalOrder() {
        AlertLog log = new AlertLog();
        log.add(alert("a1"));
        log.add(alert("a2"));
        assertEquals("a1", log.all().get(0).alertId());
        assertEquals("a2", log.all().get(1).alertId());
    }
}
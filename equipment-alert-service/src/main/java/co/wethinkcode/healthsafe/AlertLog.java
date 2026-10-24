package co.wethinkcode.healthsafe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Keeps handled alerts in arrival order and ignores repeats of the same alertId. */
final class AlertLog {

    private final Map<String, EquipmentFailureAlert> byId = new LinkedHashMap<>();

    /** Returns true if the alert was new, false if we had already handled that alertId. */
    synchronized boolean add(EquipmentFailureAlert alert) {
        return byId.putIfAbsent(alert.alertId(), alert) == null;
    }

    synchronized List<EquipmentFailureAlert> all() {
        return new ArrayList<>(byId.values());
    }
}
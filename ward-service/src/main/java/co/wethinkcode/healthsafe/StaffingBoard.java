package co.wethinkcode.healthsafe;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Remembers the most recent staffing update for each ward. */
final class StaffingBoard {

    private final Map<String, StaffingEvent> latestByWard = new ConcurrentHashMap<>();

    void record(StaffingEvent event) {
        if (event == null || event.wardId() == null) {
            return;
        }
        latestByWard.put(event.wardId().trim().toUpperCase(), event);
    }

    Optional<StaffingEvent> latest(String wardId) {
        if (wardId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(latestByWard.get(wardId.trim().toUpperCase()));
    }
}
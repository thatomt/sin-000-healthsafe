package co.wethinkcode.healthsafe;

import java.time.Instant;
import java.util.UUID;

/** The message published to equipment-failure-queue. */
public record EquipmentFailureAlert(String alertId, String wardId, String department,
                                    String equipment, String description, String reportedAt) {

    static EquipmentFailureAlert from(Ward ward, EquipmentFailureReport report) {
        String description = report.description() == null ? null : report.description().trim();
        return new EquipmentFailureAlert(
                UUID.randomUUID().toString(),
                ward.wardId(),
                ward.department(),
                report.equipment().trim(),
                description,
                Instant.now().toString());
    }
}
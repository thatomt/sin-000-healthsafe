package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Body of POST /wards/{id}/equipment-failures. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EquipmentFailureReport(String equipment, String description) {

    boolean hasEquipment() {
        return equipment != null && !equipment.isBlank();
    }
}
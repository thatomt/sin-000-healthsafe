package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One equipment failure alert, as published by ward-service. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EquipmentFailureAlert(String alertId, String wardId, String department,
                                    String equipment, String description, String reportedAt) {
}
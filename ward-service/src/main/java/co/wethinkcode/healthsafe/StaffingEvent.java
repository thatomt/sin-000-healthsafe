package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A staffing update broadcast by staffing-service. Unknown fields are ignored. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StaffingEvent(String wardId, String department, int alertLevel, List<ShiftCover> shifts) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ShiftCover(String shift, int doctors) {
    }
}
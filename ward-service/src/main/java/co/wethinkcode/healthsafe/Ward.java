package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A ward as received from ingestion-service. Unknown JSON fields are ignored. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Ward(String wardId, String wing, String department,
                   Integer bedsAvailable, List<String> notes) {
}
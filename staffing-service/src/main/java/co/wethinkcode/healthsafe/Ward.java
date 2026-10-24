package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A ward as returned by ward-service. Extra fields (wing, notes, ...) are ignored. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Ward(String wardId, String department) {
}
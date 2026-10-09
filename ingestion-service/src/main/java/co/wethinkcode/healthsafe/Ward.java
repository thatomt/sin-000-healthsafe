package co.wethinkcode.healthsafe;

import java.util.List;

/**
 * One cleaned ward record from wards-outdated.csv.
 * Fields that could not be cleaned are null, with the reason in notes.
 */
public record Ward(String wardId, String wing, String department,
                   Integer bedsAvailable, List<String> notes) {
}
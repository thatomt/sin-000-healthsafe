package co.wethinkcode.healthsafe;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class WardCatalogue {

    private WardCatalogue() {
    }

    /** Finds a ward by id, ignoring case and padding ("  w-05 " finds "W-05"). */
    static Optional<Ward> findById(List<Ward> wards, String id) {
        if (id == null) {
            return Optional.empty();
        }
        String wanted = id.trim().toUpperCase();
        return wards.stream()
                .filter(ward -> wanted.equals(ward.wardId()))
                .findFirst();
    }

    /** Distinct, alphabetically sorted department names (missing ones skipped). */
    static List<String> departments(List<Ward> wards) {
        return wards.stream()
                .map(Ward::department)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }
}
package co.wethinkcode.healthsafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

public final class WardCleaner {

    private static final Set<String> PLACEHOLDERS = Set.of("n/a", "tbd", "unknown", "-", "nan");
    private static final int MAX_BEDS = 100; // assumption: no ward has more than 100 beds
    private static final Map<String, String> DEPARTMENT_ALIASES =
            Map.of("pediatrics", "Paediatrics", "icu", "ICU");

    /** Turns one raw CSV row into a Ward, or null if the row has no ward id. */
    static Ward cleanRow(String[] row) {
        List<String> notes = new ArrayList<>();

        String id = clean(cell(row, 0));
        if (id == null) {
            return null;
        }

        String wing = clean(cell(row, 1));
        if (wing == null) {
            notes.add("wing missing");
        } else {
            wing = titleCase(wing);
        }

        String department = clean(cell(row, 2));
        if (department == null) {
            notes.add("department missing");
        } else {
            department = DEPARTMENT_ALIASES.getOrDefault(department.toLowerCase(), titleCase(department));
        }

        Integer beds = parseBeds(cell(row, 3), notes);
        return new Ward(id.toUpperCase(), wing, department, beds, notes);
    }

    private static String cell(String[] row, int index) {
        return index < row.length ? row[index] : null;
    }

    private WardCleaner() {
    }

    /** Trims, collapses repeated spaces, and turns placeholders/blank into null. */
    static String clean(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim().replaceAll("\\s+", " ");
        if (s.isEmpty() || PLACEHOLDERS.contains(s.toLowerCase())) {
            return null;
        }
        return s;
    }

    static String titleCase(String s) {
        StringBuilder out = new StringBuilder();
        for (String word : s.toLowerCase().split(" ")) {
            if (out.length() > 0) {
                out.append(' ');
            }
            out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return out.toString();
    }
    /** Returns a valid bed count, or null with the reason added to notes. */
    static Integer parseBeds(String raw, List<String> notes) {
        String s = clean(raw);
        if (s == null) {
            notes.add("bedsAvailable missing");
            return null;
        }
        try {
            int n = Integer.parseInt(s);
            if (n < 0) {
                notes.add("bedsAvailable was negative ('" + s + "'), flagged for follow-up");
                return null;
            }
            if (n > MAX_BEDS) {
                notes.add("bedsAvailable unrealistic ('" + s + "'), flagged for follow-up");
                return null;
            }
            return n;
        } catch (NumberFormatException e) {
            notes.add("bedsAvailable was non-numeric ('" + s + "'), flagged for follow-up");
            return null;
        }
    }
}

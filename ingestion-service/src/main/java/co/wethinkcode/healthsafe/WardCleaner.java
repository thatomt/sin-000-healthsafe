package co.wethinkcode.healthsafe;

import java.util.Set;

public final class WardCleaner {

    private static final Set<String> PLACEHOLDERS = Set.of("n/a", "tbd", "unknown", "-", "nan");

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
}

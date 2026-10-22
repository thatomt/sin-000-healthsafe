package co.wethinkcode.healthsafe;

/** Holds the hospital Emergency Status (0-8). Starts at 0 (normal). */
final class AlertLevelStore {

    static final int MIN_LEVEL = 0;
    static final int MAX_LEVEL = 8;

    private int level = MIN_LEVEL;

    synchronized int get() {
        return level;
    }

    /** Sets the level, or throws IllegalArgumentException (leaving the old level) if out of range. */
    synchronized void set(int newLevel) {
        if (newLevel < MIN_LEVEL || newLevel > MAX_LEVEL) {
            throw new IllegalArgumentException(
                    "level must be between " + MIN_LEVEL + " and " + MAX_LEVEL + " but was " + newLevel);
        }
        level = newLevel;
    }
}
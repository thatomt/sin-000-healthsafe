package co.wethinkcode.healthsafe;

import java.util.List;

final class ScheduleCalculator {

    static final int MIN_LEVEL = 0;
    static final int MAX_LEVEL = 8;

    private ScheduleCalculator() {
    }

    /**
     * Assumption (not a hospital standard): Day and Evening get 1 + (level + 1) / 2 doctors,
     * Night gets one fewer but never less than 1.
     */
    static Schedule calculate(String wardId, String department, int level) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new IllegalArgumentException(
                    "level must be between " + MIN_LEVEL + " and " + MAX_LEVEL + " but was " + level);
        }
        int perShift = 1 + (level + 1) / 2;
        int night = Math.max(1, perShift - 1);
        return new Schedule(wardId, department, level, List.of(
                new Schedule.ShiftCover("Day", perShift),
                new Schedule.ShiftCover("Evening", perShift),
                new Schedule.ShiftCover("Night", night)));
    }
}
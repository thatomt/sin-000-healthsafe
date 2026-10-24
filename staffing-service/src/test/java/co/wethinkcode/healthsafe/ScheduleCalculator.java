package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleCalculatorTest {

    private static int[] doctors(Schedule schedule) {
        return schedule.shifts().stream().mapToInt(Schedule.ShiftCover::doctors).toArray();
    }

    @Test
    void levelZeroIsTheMinimumCover() {
        Schedule schedule = ScheduleCalculator.calculate("W-01", "Cardiology", 0);
        assertArrayEquals3(new int[]{1, 1, 1}, doctors(schedule));
    }

    @Test
    void midLevelScalesUp() {
        assertArrayEquals3(new int[]{3, 3, 2}, doctors(ScheduleCalculator.calculate("W-01", "Cardiology", 3)));
    }

    @Test
    void levelEightIsFullCover() {
        assertArrayEquals3(new int[]{5, 5, 4}, doctors(ScheduleCalculator.calculate("W-01", "Cardiology", 8)));
    }

    @Test
    void scheduleCarriesTheWardAndDepartment() {
        Schedule schedule = ScheduleCalculator.calculate("W-05", "Paediatrics", 2);
        assertEquals("W-05", schedule.wardId());
        assertEquals("Paediatrics", schedule.department());
        assertEquals(2, schedule.alertLevel());
    }

    @Test
    void coverNeverShrinksAsTheLevelRisesAndNightIsNeverEmpty() {
        int previousDay = 0;
        for (int level = 0; level <= 8; level++) {
            int[] cover = doctors(ScheduleCalculator.calculate("W-01", "ICU", level));
            assertTrue(cover[0] >= previousDay, "day cover dropped at level " + level);
            assertTrue(cover[2] >= 1, "night shift empty at level " + level);
            previousDay = cover[0];
        }
    }

    @Test
    void rejectsLevelsOutsideTheRange() {
        assertThrows(IllegalArgumentException.class, () -> ScheduleCalculator.calculate("W-01", "ICU", -1));
        assertThrows(IllegalArgumentException.class, () -> ScheduleCalculator.calculate("W-01", "ICU", 9));
    }

    private static void assertArrayEquals3(int[] expected, int[] actual) {
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, actual);
    }
}
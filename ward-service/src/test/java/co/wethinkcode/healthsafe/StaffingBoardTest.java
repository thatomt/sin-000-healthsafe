package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaffingBoardTest {

    private static StaffingEvent event(String wardId, int level) {
        return new StaffingEvent(wardId, "Paediatrics", level,
                List.of(new StaffingEvent.ShiftCover("Day", 2)));
    }

    @Test
    void findsTheLatestEventIgnoringCaseAndPadding() {
        StaffingBoard board = new StaffingBoard();
        board.record(event("W-05", 3));
        assertEquals(3, board.latest("  w-05 ").orElseThrow().alertLevel());
    }

    @Test
    void aNewerEventReplacesTheOlderOne() {
        StaffingBoard board = new StaffingBoard();
        board.record(event("W-05", 3));
        board.record(event("W-05", 7));
        assertEquals(7, board.latest("W-05").orElseThrow().alertLevel());
    }

    @Test
    void unknownWardHasNoEvent() {
        assertTrue(new StaffingBoard().latest("W-99").isEmpty());
        assertTrue(new StaffingBoard().latest(null).isEmpty());
    }

    @Test
    void ignoresAnEventWithoutAWardId() {
        StaffingBoard board = new StaffingBoard();
        board.record(event(null, 3));
        board.record(null);
        assertTrue(board.latest("W-05").isEmpty());
    }

    @Test
    void readsTheJsonThatStaffingServiceSendsAndIgnoresExtraFields() throws Exception {
        String json = "{\"wardId\":\"W-05\",\"department\":\"Paediatrics\",\"alertLevel\":5,"
                + "\"shifts\":[{\"shift\":\"Day\",\"doctors\":4}],\"somethingNew\":1}";

        StaffingEvent parsed = new ObjectMapper().readValue(json, StaffingEvent.class);

        assertEquals("W-05", parsed.wardId());
        assertEquals(5, parsed.alertLevel());
        assertEquals(4, parsed.shifts().get(0).doctors());
    }
}
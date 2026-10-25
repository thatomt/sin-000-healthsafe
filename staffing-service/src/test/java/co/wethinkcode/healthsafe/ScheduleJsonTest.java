package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScheduleJsonTest {

    @Test
    void theBroadcastJsonHasTheFieldsWardServiceExpects() throws Exception {
        Schedule schedule = ScheduleCalculator.calculate("W-05", "Paediatrics", 5);

        JsonNode json = new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(schedule));

        assertEquals("W-05", json.get("wardId").asText());
        assertEquals("Paediatrics", json.get("department").asText());
        assertEquals(5, json.get("alertLevel").asInt());
        assertEquals(3, json.get("shifts").size());
        assertEquals("Day", json.get("shifts").get(0).get("shift").asText());
        assertEquals(4, json.get("shifts").get(0).get("doctors").asInt());
    }
}
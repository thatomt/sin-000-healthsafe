package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class StaffingServiceApp {

    public static void main(String[] args) {
        ServiceCaller caller = new ServiceCaller();
        WardClient wards = new WardClient(caller,
                System.getenv().getOrDefault("WARD_SERVICE_URL", "http://localhost:7031"));
        AlertLevelClient alertLevels = new AlertLevelClient(caller,
                System.getenv().getOrDefault("ALERT_LEVEL_SERVICE_URL", "http://localhost:7032"));
        StaffingEventPublisher publisher = new StaffingEventPublisher();
        ObjectMapper mapper = new ObjectMapper();

        Javalin app = Javalin.create().start(7033);

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/staffing/{wardId}", ctx -> {
            String wardId = ctx.pathParam("wardId").trim();

            Optional<Ward> ward = wards.findWard(wardId);
            if (ward.isEmpty()) {
                ctx.status(404).json(Map.of("error", "ward not found: " + wardId));
                return;
            }

            int level = alertLevels.currentLevel();
            try {
                Schedule schedule = ScheduleCalculator.calculate(ward.get().wardId(), ward.get().department(), level);
                broadcast(publisher, mapper, schedule);
                ctx.json(schedule);
            } catch (IllegalArgumentException e) {
                ctx.status(502).json(Map.of("error", "alert-level-service sent an invalid level: " + e.getMessage()));
            }
        });

        // A downstream service that is down, slow or broken becomes a clear 502, not a crash.
        app.exception(IOException.class, (e, ctx) ->
                ctx.status(502).json(Map.of("error", e.getMessage())));
    }

    /** Announces the schedule on the topic. A broker problem is logged but never fails the request. */
    private static void broadcast(StaffingEventPublisher publisher, ObjectMapper mapper, Schedule schedule) {
        try {
            publisher.publish(mapper.writeValueAsString(schedule));
        } catch (JMSException | JsonProcessingException e) {
            System.err.println("Could not broadcast staffing update (continuing): " + e.getMessage());
        }
    }
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)

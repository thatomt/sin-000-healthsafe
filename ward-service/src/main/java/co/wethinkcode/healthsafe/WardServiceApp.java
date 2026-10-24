package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WardServiceApp {

    public static void main(String[] args) {
        String ingestionUrl = System.getenv().getOrDefault("INGESTION_URL", "http://localhost:7030");
        IngestionClient ingestion = new IngestionClient(ingestionUrl);

        Javalin app = Javalin.create().start(7031);

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/wards", ctx -> ctx.json(ingestion.fetchWards()));

        app.get("/wards/{id}", ctx -> {
            String id = ctx.pathParam("id");
            Optional<Ward> ward = WardCatalogue.findById(ingestion.fetchWards(), id);
            if (ward.isPresent()) {
                ctx.json(ward.get());
            } else {
                ctx.status(404).json(Map.of("error", "ward not found: " + id.trim()));
            }
        });

        app.get("/departments", ctx -> {
            List<String> departments = WardCatalogue.departments(ingestion.fetchWards());
            ctx.json(departments);
        });

        // Any failure talking to ingestion-service becomes a clear 502, not a crash.
        app.exception(IOException.class, (e, ctx) ->
                ctx.status(502).json(Map.of("error", "ingestion-service unavailable: " + e.getMessage())));
    }
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.

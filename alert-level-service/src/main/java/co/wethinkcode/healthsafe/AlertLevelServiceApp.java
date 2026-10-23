package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

import java.util.Map;

public class AlertLevelServiceApp {

    /** Shape of the PUT body: {"level": 5}. Integer (not int) so a missing value shows up as null. */
    record LevelRequest(Integer level) {
    }

    public static void main(String[] args) {
        AlertLevelStore store = new AlertLevelStore();

        Javalin app = Javalin.create().start(7032);

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/alert-level", ctx -> ctx.json(Map.of("level", store.get())));

        app.put("/alert-level", ctx -> {
            LevelRequest request;
            try {
                request = ctx.bodyAsClass(LevelRequest.class);
            } catch (RuntimeException e) {
                ctx.status(400).json(Map.of("error", "body must be JSON like {\"level\": 5}"));
                return;
            }
            if (request == null || request.level() == null) {
                ctx.status(400).json(Map.of("error", "missing \"level\" in request body"));
                return;
            }
            try {
                store.set(request.level());
                ctx.json(Map.of("level", store.get()));
            } catch (IllegalArgumentException e) {
                ctx.status(400).json(Map.of("error", e.getMessage()));
            }
        });
    }
}
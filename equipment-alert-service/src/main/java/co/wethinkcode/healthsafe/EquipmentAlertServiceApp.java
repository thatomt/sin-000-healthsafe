package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

public class EquipmentAlertServiceApp {

    public static void main(String[] args) {
        AlertLog alerts = new AlertLog();
        new EquipmentAlertConsumer(alerts).start();

        Javalin app = Javalin.create().start(7034);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/alerts", ctx -> ctx.json(alerts.all()));
    }
}
        // TODO (Uses a Queue to guarantee delivery of critical medical equipment failure alerts.)
        // Mechanism: ActiveMQ Queue (guaranteed delivery)

// MQ TODO: consumes ActiveMQ queue MqConfig.QUEUE at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// Producer: ward-service publishes here when it detects an equipment failure on one of its wards.

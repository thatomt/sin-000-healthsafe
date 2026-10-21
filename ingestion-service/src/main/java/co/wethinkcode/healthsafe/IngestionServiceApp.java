package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import io.javalin.Javalin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IngestionServiceApp {

    public static void main(String[] args) throws IOException, CsvValidationException {
        List<Ward> wards = loadWards();

        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/wards", ctx -> ctx.json(wards));
    }

    private static List<Ward> loadWards() throws IOException, CsvValidationException {
        InputStream in = IngestionServiceApp.class.getResourceAsStream("/wards-outdated.csv");
        if (in == null) {
            throw new IOException("wards-outdated.csv not found on the classpath");
        }
        try (CSVReader reader = new CSVReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            reader.readNext(); // skip the header row
            List<String[]> rows = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(row);
            }
            return WardCleaner.cleanAll(rows);
        }
    }
}
package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class IngestionClient {

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;

    public IngestionClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /** Fetches the cleaned wards. Throws IOException if ingestion is unreachable or misbehaves. */
    public List<Ward> fetchWards() throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/wards"))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("ingestion-service returned HTTP " + response.statusCode());
            }
            return mapper.readValue(response.body(), new TypeReference<List<Ward>>() {
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted while calling ingestion-service", e);
        }
    }
}
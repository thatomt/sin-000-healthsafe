package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

final class WardClient {

    private final ServiceCaller caller;
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    WardClient(ServiceCaller caller, String baseUrl) {
        this.caller = caller;
        this.baseUrl = baseUrl;
    }

    /** Empty if ward-service says 404. Any other failure throws IOException. */
    Optional<Ward> findWard(String wardId) throws IOException {
        String encoded = URLEncoder.encode(wardId, StandardCharsets.UTF_8).replace("+", "%20");
        ServiceCaller.Response response = caller.get("ward-service", baseUrl + "/wards/" + encoded);
        if (response.status() == 404) {
            return Optional.empty();
        }
        if (response.status() != 200) {
            throw new IOException("ward-service returned HTTP " + response.status());
        }
        return Optional.of(mapper.readValue(response.body(), Ward.class));
    }
}
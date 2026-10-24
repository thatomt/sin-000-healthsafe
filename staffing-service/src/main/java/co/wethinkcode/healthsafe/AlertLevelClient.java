package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

final class AlertLevelClient {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record LevelResponse(Integer level) {
    }

    private final ServiceCaller caller;
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    AlertLevelClient(ServiceCaller caller, String baseUrl) {
        this.caller = caller;
        this.baseUrl = baseUrl;
    }

    int currentLevel() throws IOException {
        ServiceCaller.Response response = caller.get("alert-level-service", baseUrl + "/alert-level");
        if (response.status() != 200) {
            throw new IOException("alert-level-service returned HTTP " + response.status());
        }
        LevelResponse parsed = mapper.readValue(response.body(), LevelResponse.class);
        if (parsed.level() == null) {
            throw new IOException("alert-level-service sent a reply without a level");
        }
        return parsed.level();
    }
}
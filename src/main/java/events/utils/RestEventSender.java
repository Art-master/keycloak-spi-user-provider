package events.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.utils.api.Sender;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.services.managers.AuthenticationManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class RestEventSender implements Sender {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Sender instance;

    private RestEventSender() {
    }

    public static Sender instance() {
        if (instance == null) {
            instance = new RestEventSender();
        }
        return instance;
    }

    @Override
    public boolean send(Object body, String url, AuthenticationManager.AuthResult authenticate) {
        log.info("Sending data {} to URL {}", body, url);

        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(body);

        } catch (JsonProcessingException e) {
            log.error("Failed to create json string");
            return false;
        }
        return send(jsonRequestBody, url, authenticate);
    }

    private boolean send(String json, String url, AuthenticationManager.AuthResult authenticate) {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authenticate.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        log.info("Sending data to url {}", request);

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String string = response.body();
            log.info("Sending data to url. Request: {}", string);
            return true;

        } catch (IOException | InterruptedException e) {
            log.error("Failed to send request to user-service {}", request);
            return false;
        }
    }

}

package events.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.utils.api.Sender;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.keycloak.services.managers.AuthenticationManager;

import java.io.IOException;

@Slf4j
public class RestEventSender implements Sender {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

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
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authenticate.getToken())
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String string = response.body() != null ? response.body().string() : "";
            log.info("Sending data to url. Request: {}", string);
            return true;

        } catch (IOException e) {
            log.error("Failed to send request to user-service {}", request);
            return false;
        }
    }

}

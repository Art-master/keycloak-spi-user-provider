import lombok.Getter;

@Getter
public enum ExtConfig {
    USER_SERVICE_BASE_URL("userServiceBaseUrl");

    private final String configKey;

    ExtConfig(String key) {
        configKey = key;
    }
}

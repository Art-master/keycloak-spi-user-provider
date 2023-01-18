import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

    protected static final List<ProviderConfigProperty> configMetadata;

    static {
        configMetadata = ProviderConfigurationBuilder.create()
                .property()
                .name(ExtConfig.USER_SERVICE_BASE_URL.getConfigKey())
                .label("Base user service url")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("http://localhost:8082/")
                .helpText("Base url for user service")
                .add()
                .build();
    }

    @Override
    public CustomUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new CustomUserStorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return "gn-user-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigMetadata() {
        return configMetadata;
    }

    @Override
    public List<ProviderConfigProperty> getCommonProviderConfigProperties() {
        return configMetadata;
    }
}

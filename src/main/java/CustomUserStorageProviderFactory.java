import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

    @Override
    public CustomUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        Repository repository = new Repository();
        return new CustomUserStorageProvider(keycloakSession, componentModel, repository);
    }

    @Override
    public String getId() {
        return "gn-user-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigMetadata() {
        return ProviderConfigurationBuilder.create()
                .property()
                .name(ExtConfig.JDBC_DRIVER.getConfigKey())
                .label("JDBC Driver Class")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .helpText("Fully qualified class name of the JDBC driver")
                .add()

                .property()
                .name(ExtConfig.JDBC_URL.getConfigKey())
                .label("JDBC URL")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("jdbc:sqlserver://<ip>:<port>;DatabaseName=<db name>;")
                .helpText("JDBC URL used to connect to the user database")
                .add()

                .property()
                .name(ExtConfig.DB_USERNAME.getConfigKey())
                .label("Database User")
                .type(ProviderConfigProperty.STRING_TYPE)
                .helpText("Username used to connect to the database")
                .defaultValue("sa")
                .add()

                .property()
                .name(ExtConfig.DB_PASSWORD.getConfigKey())
                .label("Database Password")
                .type(ProviderConfigProperty.STRING_TYPE)
                .helpText("Password used to connect to the database")
                .defaultValue("Keycloak@123")
                .secret(true)
                .add()

                .property()
                .name(ExtConfig.DB_USERS_TABLE_NAME.getConfigKey())
                .label("Users Table Name")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("us_users1")
                .helpText("Name of the users table in the database")
                .add()

                .property()
                .name(ExtConfig.DB_ROLES_TABLE_NAME.getConfigKey())
                .label("Roles Table Name")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("us_roles")
                .helpText("Name of the roles table in the database")
                .add()

                .build();
    }
}

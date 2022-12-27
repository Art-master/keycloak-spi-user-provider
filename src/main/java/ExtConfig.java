import lombok.Getter;

@Getter
public enum ExtConfig {
    JDBC_DRIVER("jdbcDriver"),
    JDBC_URL("jdbcUrl"),
    DB_USERNAME("dbUsername"),
    DB_PASSWORD("dbPassword"),
    VALIDATION_QUERY("validationQuery"),
    DB_USERS_TABLE_NAME("usersTableName"),
    DB_ROLES_TABLE_NAME("rolesTableName"),
    USER_FIRST_NAME_FIELD_NAME("firstName"),
    USER_LAST_NAME_FIELD_NAME("lastName"),
    USER_USERNAME_FIELD_NAME("username"),
    USER_PASSWORD_FIELD_NAME("password"),
    USER_EMAIL_FIELD_NAME("email"),
    ROLE_NAME_FIELD_NAME("roleName"),
    ROLE_USER_ID_FIELD_NAME("roleUserId");

    private final String configKey;

    ExtConfig(String key) {
        configKey = key;
    }
}

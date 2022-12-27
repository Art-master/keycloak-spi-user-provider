import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DbConnector {

    public static Connection getConnection(ComponentModel config) throws SQLException {
        String driverClass = config.get(ExtConfig.JDBC_DRIVER.getConfigKey());
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException nfe) {
            throw new RuntimeException("Invalid JDBC driver: " + driverClass + ". Please check if your driver if properly installed");
        }
        return DriverManager.getConnection(config.get(ExtConfig.JDBC_URL.getConfigKey()),
                config.get(ExtConfig.DB_USERNAME.getConfigKey()),
                config.get(ExtConfig.DB_PASSWORD.getConfigKey()));
    }
}

package events.api;

import org.keycloak.events.admin.AdminEvent;
import org.keycloak.services.managers.AuthenticationManager;

public interface EventProcessor {
    boolean compareAndSend(AdminEvent event, AuthenticationManager.AuthResult authenticate);
}

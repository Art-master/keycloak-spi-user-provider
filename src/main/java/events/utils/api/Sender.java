package events.utils.api;

import org.keycloak.services.managers.AuthenticationManager;

public interface Sender {

    boolean send(Object body, String url, AuthenticationManager.AuthResult authenticate);
}

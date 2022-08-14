package events.user_events;

import events.api.EventProcessor;
import events.api.EventTypeMetaInfo;
import events.dto.UserEventRequest;
import events.utils.RestEventSender;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.services.managers.AuthenticationManager;

public class UsersEventProcessor extends EventTypeMetaInfo implements EventProcessor {

    private static final String URL = System.getenv("USER_MANAGEMENT_SERVICE_URI");

    @Override
    protected String getOperationType() {
        return "ACTION";
    }

    protected String getResourceType() {
        return "users";
    }

    protected String getClientUrl() {
        return URL;
    }

    protected String getResourceSubType() {
        return "";
    }

    public boolean compareAndSend(AdminEvent event, AuthenticationManager.AuthResult authenticate) {
        if (!needToSend(event)) return false;

        String[] resourcePath = event.getResourcePath().split("/");
        var login = resourcePath[1];
        var pass = resourcePath[2];
        var dto = new UserEventRequest(login, pass);

        var transport = RestEventSender.instance();
        return transport.send(dto, getClientUrl(), authenticate);
    }

    protected boolean needToSend(AdminEvent event) {
        String[] resourcePath = event.getResourcePath().split("/");
        return getOperationType().equals(event.getOperationType().toString()) &&
                getResourceType().equals(resourcePath[0]);
    }
}

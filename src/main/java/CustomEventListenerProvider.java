import events.api.EventProcessor;
import events.user_events.UsersEventProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.services.managers.AuthenticationManager;

import java.util.LinkedList;

@Slf4j
@AllArgsConstructor
public class CustomEventListenerProvider implements EventListenerProvider {

    private static final LinkedList<EventProcessor> eventTypes = new LinkedList<>();

    private final AuthenticationManager.AuthResult authenticate;

    static {
        eventTypes.add(new UsersEventProcessor());
    }

    @Override
    public void onEvent(Event event) {

    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        eventTypes.forEach(e -> e.compareAndSend(adminEvent, authenticate));
    }

    @Override
    public void close() {

    }
}

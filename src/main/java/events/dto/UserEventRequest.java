package events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEventRequest {

    private String login;
    private String password;
}

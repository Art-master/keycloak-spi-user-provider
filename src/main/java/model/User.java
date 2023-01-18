package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.keycloak.models.RealmModel;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private boolean enabled;
    private Long created;
    private List<String> roles = new ArrayList<>();


    public static User fromRealmModel(RealmModel model) {
        User user = new User();
        user.setUsername(model.getName());
        user.setEmail(model.getEmailTheme());
        return new User(); //TODO
    }
}

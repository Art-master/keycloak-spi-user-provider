package model;

import lombok.Data;
import org.keycloak.models.RealmModel;

import java.util.List;

@Data
public class User {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private boolean enabled;
    private Long created;
    private List<String> roles;


    public static User fromRealmModel(RealmModel model) {
        return new User(); //TODO
    }
}

package model;

import lombok.Data;

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
}

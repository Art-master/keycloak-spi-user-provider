import model.User;
import org.keycloak.models.UserModel;

import java.util.List;

public class Repository {

    public int getUsersCount() {
        return 0;
    }

    public User findUserById(String externalId) {
        return null;
    }

    public boolean validateCredentials(String username, String challengeResponse) {
        return false;
    }

    public boolean updateCredentials(String username, String challengeResponse) {
        return false;
    }

    public User findUserByUsernameOrEmail(String username) {
        return null;
    }

    public List<User> findUsers(String search) {
        return null;
    }

    public List<User> getAllUsers() {
        return null;
    }

    public UserModel addUser(String username) {
        return null;
    }

    public boolean removeUser(UserModel user) {
        return false;
    }
}

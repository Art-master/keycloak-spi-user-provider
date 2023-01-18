import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import model.User;
import model.UserAdapter;
import org.jetbrains.annotations.Nullable;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.services.ErrorResponseException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class CustomUserStorageProvider implements UserStorageProvider, UserLookupProvider, UserQueryProvider, CredentialInputUpdater,
        CredentialInputValidator, UserRegistrationProvider {

    private final KeycloakSession session;
    private final ComponentModel model;

    private final String baseUrl;

    public CustomUserStorageProvider(KeycloakSession session, ComponentModel componentModel) {
        this.session = session;
        this.model = componentModel;

        baseUrl = getBaseUrl();
    }

    private String getBaseUrl() {
        if (baseUrl == null || baseUrl.isEmpty()) {
            return System.getenv("USER_MANAGEMENT_SERVICE_URI") + "/users";
        }
        return baseUrl;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        boolean answer;
        try {
            SimpleHttp http = SimpleHttp.doGet(getBaseUrl() + "/validate_credentials", session);
            http.param("name", user.getUsername());
            http.param("password", cred.getChallengeResponse());

            answer = http.asJson(Boolean.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErrorResponseException("Error", "Request error", Response.Status.BAD_REQUEST);
        }
        return answer;
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        String externalId = StorageId.externalId(user.getId());

        boolean answer;
        try {
            SimpleHttp http = SimpleHttp.doGet(getBaseUrl() + "/" + externalId + "/update_credentials", session);
            http.param("name", user.getUsername());
            http.param("password", cred.getChallengeResponse());

            answer = http.asJson(Boolean.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErrorResponseException("Error", "Request error", Response.Status.BAD_REQUEST);
        }
        return answer;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realm, UserModel user) {
        return Stream.empty();
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        String externalId = StorageId.externalId(id);

        if (Objects.isNull(externalId)) return null;

        return getUserByEmail(realm, externalId);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        SimpleHttp http = SimpleHttp.doGet(getBaseUrl() + "/get_by_login", session);
        http.param("login", username);

        return sendRequest(realm, http);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        SimpleHttp http = SimpleHttp.doGet(getBaseUrl() + "/get_by_email", session);
        http.param("email", email);

        return sendRequest(realm, http);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        String url = getBaseUrl() + "/get_users_count";
        SimpleHttp http = SimpleHttp.doGet(url, session);
        long count;
        try {
            count = http.asJson(Long.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErrorResponseException("Error", "Request error", Response.Status.BAD_REQUEST);
        }

        return (int) count;
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search) {
        return searchForUser(realm, search, null, null);
    }

    public Stream<UserModel> searchForUser(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        SimpleHttp http = SimpleHttp.doGet(getBaseUrl() + "/search", session);
        http.param("data", search);
        if (firstResult != null) http.param("firstResult", String.valueOf(firstResult));
        if (maxResults != null) http.param("maxResult", String.valueOf(maxResults));

        List<User> users;

        try {
            users = http.asJson(new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErrorResponseException("Error", "Request error", Response.Status.BAD_REQUEST);
        }

        return users.stream().map(user -> new UserAdapter(session, realm, model, user));
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params) {
        return searchForUser(realm, "*", null, null);
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        return searchForUser(realm, search, firstResult, maxResults);
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        return searchForUser(realm, "*", firstResult, maxResults); //TODO
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty();
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        throw new ErrorResponseException("Error", "Creating a user from the interface is prohibited", Response.Status.CONFLICT);
    }

    @Nullable
    private UserModel sendRequest(RealmModel realm, SimpleHttp http) {
        User user;
        try {
            user = http.asJson(User.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErrorResponseException("Error", "Request error", Response.Status.BAD_REQUEST);
        }

        if (user != null && user.getId() != null) {
            return new UserAdapter(session, realm, model, user);
        }
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        String email = StorageId.externalId(user.getId());
        SimpleHttp http = SimpleHttp.doDelete(getBaseUrl() + "/delete_by_email", session);
        http.param("email", email);

        boolean isSuccess;

        try {
            isSuccess = http.asJson(Boolean.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErrorResponseException("Error", "Deleting user error", Response.Status.BAD_REQUEST);
        }

        return isSuccess;
    }

    @Override
    public void close() {
    }
}

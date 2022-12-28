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
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CustomUserStorageProvider implements UserStorageProvider, UserLookupProvider, UserQueryProvider, CredentialInputUpdater,
        CredentialInputValidator, UserRegistrationProvider {

    private final KeycloakSession session;
    private final ComponentModel model;

    private final String baseUrl;

    public CustomUserStorageProvider(KeycloakSession session, ComponentModel componentModel) {
        this.session = session;
        this.model = componentModel;

        baseUrl = componentModel.get(ExtConfig.USER_SERVICE_BASE_URL.getConfigKey());
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
        boolean answer = false;
        try {
            SimpleHttp http = SimpleHttp.doGet(baseUrl + "/validate_credentials", session);
            http.param("name", user.getUsername());
            http.param("password", cred.getChallengeResponse());

            answer = http.asJson(Boolean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;

        boolean answer = false;
        try {
            SimpleHttp http = SimpleHttp.doGet(baseUrl + "/update_credentials", session);
            http.param("name", user.getUsername());
            http.param("password", cred.getChallengeResponse());

            answer = http.asJson(Boolean.class);
        } catch (IOException e) {
            e.printStackTrace();
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

        SimpleHttp http = SimpleHttp.doGet(baseUrl + externalId, session);
        User user = null;
        try {
            user = http.asJson(User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new UserAdapter(session, realm, model, user);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        return getUserModel(realm, "user_name", username);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return getUserModel(realm, "email", email);
    }

    @Nullable
    private UserModel getUserModel(RealmModel realm, String paramName, String paramValue) {
        SimpleHttp http = SimpleHttp.doGet(baseUrl, session);
        http.param(paramName, paramValue);
        return sendRequest(realm, http);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        String url = baseUrl + "/get_users_count";
        SimpleHttp http = SimpleHttp.doGet(url, session);
        long count = 0;
        try {
            count = http.asJson(Long.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (int) count;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search) {
        SimpleHttp http = SimpleHttp.doGet(baseUrl, session);
        http.param("search", search);

        List<User> users = new ArrayList<>();

        try {
            users = http.asJson(List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users.stream().map(user -> new UserAdapter(session, realm, model, user));
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        return searchForUserStream(realm, search);
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        return searchForUserStream(realm, ""); //TODO
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
        SimpleHttp http = SimpleHttp.doPost(baseUrl, session);
        http.json(User.fromRealmModel(realm));
        return sendRequest(realm, http);
    }

    @Nullable
    private UserModel sendRequest(RealmModel realm, SimpleHttp http) {
        User user = null;
        try {
            user = http.asJson(User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (user != null) {
            return new UserAdapter(session, realm, model, user);
        }
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        SimpleHttp http = SimpleHttp.doDelete(baseUrl, session);
        http.param("id", user.getId());

        boolean isSuccess = false;

        try {
            isSuccess = http.asJson(Boolean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isSuccess;
    }

    @Override
    public void close() {

    }
}

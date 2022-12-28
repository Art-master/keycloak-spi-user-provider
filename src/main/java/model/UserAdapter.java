package model;

import model.User;
import model.UserRoleModel;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.*;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserAdapter extends AbstractUserAdapter {

    private final User user;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, User user) {
        super(session, realm, storageProviderModel);

        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getFirstName() {
        return user.getFirstName();
    }

    @Override
    public String getLastName() {
        return user.getLastName();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new LegacyUserCredentialManager(session, realm, this);
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    @Override
    public Long getCreatedTimestamp() {
        return user.getCreated();
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, getUsername());
        attributes.add(UserModel.EMAIL, getEmail());
        attributes.add(UserModel.FIRST_NAME, getFirstName());
        attributes.add(UserModel.LAST_NAME, getLastName());
        return attributes;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        if (name.equals(UserModel.USERNAME)) {
            return Stream.of(getUsername());
        }
        return Stream.empty();
    }

    @Override
    protected Set<RoleModel> getRoleMappingsInternal() {
        if (user.getRoles() != null) {
            return user.getRoles().stream().map(roleName -> new UserRoleModel(roleName, realm)).collect(Collectors.toSet());
        }
        return Set.of();
    }
}

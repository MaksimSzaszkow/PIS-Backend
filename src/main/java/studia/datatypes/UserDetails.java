package studia.datatypes;

import java.util.Collection;
import java.util.Map;

import io.micronaut.security.authentication.Authentication;

public class UserDetails implements Authentication {
    Collection<String> roles;
    Map<String, Object> attributes;
    String name;

    public UserDetails(String name, Collection<String> roles, Map<String, Object> attributes) {
        this.name = name;
        this.roles = roles;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }

    @Override
    public String getName() {
        return name;
    }
}

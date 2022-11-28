package studia.service;

import java.util.Optional;

public class AuthHeader {
    private final String type;
    private final String token;

    public AuthHeader(String type, String token) {
        this.type = type;
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public static Optional<AuthHeader> parse(String authentication) {
        var opt = Optional.<AuthHeader>empty();
        if (authentication != null) {
            var parts = authentication.split("\\s+");
            if (parts.length == 2) {
                var auth = new AuthHeader(parts[0].trim(), parts[1].trim());
                opt = Optional.of(auth);
            }
        }
        return opt;
    }
}
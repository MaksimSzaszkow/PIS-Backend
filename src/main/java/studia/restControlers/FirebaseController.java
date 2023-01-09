
package studia.restControlers;

import static io.micronaut.http.HttpHeaders.AUTHORIZATION;

import studia.utils.*;

import java.util.*;

import javax.inject.Inject;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import studia.datatypes.SetRole;
import studia.datatypes.UserDetails;
import studia.service.Firebase;

@Controller("/firebase")
@Secured(SecurityRule.IS_ANONYMOUS)
public class FirebaseController {

    JwtTokenGenerator jwtTokenGenerator;
    Firebase firebase;

    @Inject
    public FirebaseController(JwtTokenGenerator jwtTokenGenerator, Firebase firebase) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.firebase = firebase;
    }

    @Get(uri = "/", produces = "text/plain")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public String index() {
        return "Example Response";
    }

    @Post(uri = "/auth", produces = "application/json")
    public Optional<AuthHeader> auth(HttpRequest<?> request) throws FirebaseAuthException {
        var auth = request.getHeaders().get("Authorization");
        var opt = AuthHeader.parse(auth);
        var out = Optional.<AuthHeader>empty();
        if (opt.isPresent()) {
            var authHeader = opt.get();
            var attributes = new HashMap<String, Object>();
            attributes.put("role", "admin");

            var firebaseToken = firebase.verifyIdToken(authHeader.getToken());
            var userDetails = new UserDetails(firebaseToken.getName(),
                    Collections.emptyList(),
                    firebaseToken.getClaims());
            var jwt = jwtTokenGenerator.generateToken(userDetails, 3600);
            out = jwt.map(t -> new AuthHeader("Bearer", t));
        }
        return out;
    }

    @Post("/set-user-role")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public void set_user_role(@Header(AUTHORIZATION) String authorization, @Body SetRole setRole) {
        try {
            if (Objects.equals(UserRole.getRole(authorization), "admin")) {
                var claims = new HashMap<String, Object>();
                claims.put("role", setRole.role);
                FirebaseAuth.getInstance().setCustomUserClaims(setRole.uid, claims);
            }
        } catch (FirebaseAuthException e) {
            throw new AssertionError(e);
        }
    }

    @Get("/whats-my-role")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public String whats_my_role(@Header(AUTHORIZATION) String authorization) {
        return UserRole.getRole(authorization);
    }
}
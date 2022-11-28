package studia.restControlers;

import javax.inject.Inject;

import studia.service.Firebase;
import studia.service.AuthHeader;

import com.google.firebase.auth.FirebaseAuthException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;

import java.util.Collections;
import java.util.Optional;

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

    @Get(uri="/", produces="text/plain")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public String index() {
        return "Example Response";
    }

    @Post("/auth")
    public Optional<AuthHeader> auth(HttpRequest<?> request) throws FirebaseAuthException {
        var auth = request.getHeaders().get("Authorization");
        var opt = AuthHeader.parse(auth);
        var out = Optional.<AuthHeader>empty();
        if (opt.isPresent()) {
            var authHeader = opt.get();
            var firebaseToken = firebase.verifyIdToken(authHeader.getToken());
            var userDetails = new Authentication(firebaseToken.getName(), 
                                              Collections.emptyList(), 
                                              firebaseToken.getClaims());
            var jwt = jwtTokenGenerator.generateToken(userDetails, 3600);
            out = jwt.map(t -> new AuthHeader("Bearer", t));
        }
        return out;
    }
}
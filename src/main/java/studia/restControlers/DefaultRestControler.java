package studia.restControlers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;

//@Secured(SecurityRule.IS_AUTHENTICATED)
//@Controller("/")
//public class DefaultRestControler{
//
//  @Get
//  @Produces(MediaType.TEXT_PLAIN)
//  public String index() {
//    return "Hello World";
//  }
//}
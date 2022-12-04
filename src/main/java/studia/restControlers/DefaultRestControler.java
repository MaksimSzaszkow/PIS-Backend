package studia.restControlers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/")
@Secured(SecurityRule.IS_ANONYMOUS)
public class DefaultRestControler{
  @Get
  @Produces(MediaType.TEXT_PLAIN)
  public String index() {
    return "Hello World";
  }
}
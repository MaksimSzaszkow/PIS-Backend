package studia.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;

import static io.micronaut.http.MediaType.TEXT_PLAIN;
import static io.micronaut.http.HttpStatus.UNAUTHORIZED;
import static io.micronaut.http.HttpStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest
public class AuthProviderTest {

    @Inject
    @Client("/")
    HttpClient client;

    static final String USERNAME = "sherlock";
    static final String PASSWORD = "password";

    @Test
    void authenticationTestFail() {
        assertThrows(HttpClientResponseException.class, 
            () -> client.toBlocking().exchange(HttpRequest.GET("/").accept(TEXT_PLAIN)));
        
        try{
            client.toBlocking().exchange(HttpRequest.GET("/").accept(TEXT_PLAIN));
        }
        catch (HttpClientResponseException e){
            assertEquals(UNAUTHORIZED, e.getStatus());
        }
    }

    @Test
    void authenticationTestPass() {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(USERNAME, PASSWORD);
        HttpRequest request = HttpRequest.POST("/login", creds);
        HttpResponse<BearerAccessRefreshToken> rsp = client.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        
        assertEquals(OK, rsp.getStatus());

        BearerAccessRefreshToken bearerAccessRefreshToken = rsp.body();
        
        assertEquals(USERNAME, bearerAccessRefreshToken.getUsername());
        
        String accessToken = bearerAccessRefreshToken.getAccessToken();
        HttpRequest requestWithAuthorization = HttpRequest.GET("/verify-auth")
                .accept(TEXT_PLAIN)
                .bearerAuth(accessToken);
        HttpResponse<String> response = client.toBlocking().exchange(requestWithAuthorization, String.class);
    
        assertEquals(OK, response.getStatus());
        assertEquals("{test=Hello from backend}", response.getBody().get());
    }
    
}

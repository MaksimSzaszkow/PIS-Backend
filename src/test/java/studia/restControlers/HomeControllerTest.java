package studia.restControlers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static io.micronaut.http.HttpStatus.UNAUTHORIZED;
import static io.micronaut.http.MediaType.TEXT_PLAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class HomeControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void controllerTestUnauthorized() {
        assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.GET("/verify-auth").accept(TEXT_PLAIN)));

        try{
            client.toBlocking().exchange(HttpRequest.GET("/verify-auth").accept(TEXT_PLAIN));
        }
        catch (HttpClientResponseException e){
            assertEquals(e.getStatus(), UNAUTHORIZED);
        }
    }

}

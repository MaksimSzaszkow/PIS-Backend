package studia.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import static io.micronaut.http.MediaType.TEXT_PLAIN;
import static io.micronaut.http.HttpStatus.UNAUTHORIZED;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.inject.Inject;

@MicronautTest
public class AuthProviderTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void authenticationTest() {
        assertThrows(HttpClientResponseException.class, 
            () -> client.toBlocking().exchange(HttpRequest.GET("/").accept(TEXT_PLAIN)));
        
        try{
            client.toBlocking().exchange(HttpRequest.GET("/").accept(TEXT_PLAIN));
        }
        catch (HttpClientResponseException e){
            assertEquals(e.getStatus(), UNAUTHORIZED);
        }
    }
    
}

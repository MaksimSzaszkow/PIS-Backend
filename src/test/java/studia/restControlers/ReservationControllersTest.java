package studia.restControlers;

import com.google.firebase.auth.FirebaseAuthException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import studia.datatypes.EditReservationRequest;
import studia.datatypes.ReservationData;
import studia.datatypes.UserDetails;

import java.util.Collections;
import java.util.Objects;

import static io.micronaut.http.HttpStatus.OK;
import static io.micronaut.http.MediaType.APPLICATION_JSON;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static io.micronaut.http.HttpStatus.UNAUTHORIZED;
import static io.micronaut.http.MediaType.TEXT_PLAIN;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservationControllersTest {
/*
    @Inject
    JwtTokenGenerator jwtTokenGenerator;


    private static final String USERNAME = "testUser";
    private static final UserDetails USER = new UserDetails(USERNAME, Collections.emptyList(), Collections.emptyMap());

    private final ReservationData reservationData = new ReservationData(
            USERNAME, "01.01.1970", 12, "room 1"
    );

    private String token;

    private void setToken(String token) {
        this.token = token;
    }

    private void authenticate() {
        jwtTokenGenerator.generateToken(USER, 120).ifPresent(this::setToken);
    }

    private ReservationData[] fetchReservations() {
        HttpRequest<Object> reservationRequest = HttpRequest.GET("/reservation/my-reservations").accept(APPLICATION_JSON)
                .bearerAuth(token);
        HttpResponse<ReservationData[]> reservationResponse = client.toBlocking()
                .exchange(reservationRequest, ReservationData[].class);

        assertEquals(OK, reservationResponse.getStatus());
        return reservationResponse.body();
    }


    private void addReservation(String token) {
        HttpRequest<ReservationData> addReservationRequest = HttpRequest.POST("/reservation/add-reservation", reservationData)
                .accept(APPLICATION_JSON)
                .bearerAuth(token);
        HttpResponse<String> addReservationResponse = client.toBlocking().exchange(addReservationRequest);

        assertEquals(OK, addReservationResponse.getStatus());
    }

    private void addWrongReservation(ReservationData data, String errorMessage) {
        HttpRequest<ReservationData> addReservationRequest = HttpRequest.POST(
                        "/reservation/add-reservation",
                        data
                ).accept(APPLICATION_JSON)
                .bearerAuth(token);
        assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(addReservationRequest),
                errorMessage
        );
    }

    private void checkReservationData(ReservationData[] reservations) {
        ReservationData testReservation = Objects.requireNonNull(reservations)[0];
        assertEquals(reservationData.getTime(), testReservation.getTime());
        assertEquals(reservationData.getUser(), testReservation.getUser());
        assertEquals(reservationData.getDate(), testReservation.getDate());
        assertEquals(reservationData.getRoom(), testReservation.getRoom());
    }


    @Inject
    @Client("/")
    HttpClient client;


    @Test
    @Order(1)
    void controllerTestUnauthorized() {
        assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.GET("/reservation/my-reservations").accept(TEXT_PLAIN)));

        try {
            client.toBlocking().exchange(HttpRequest.GET("/reservation/my-reservations").accept(TEXT_PLAIN));
        } catch (HttpClientResponseException e) {
            assertEquals(e.getStatus(), UNAUTHORIZED);
        }
    }


    @Test
    @Order(2)
    void usersReservationFetchTest() {
        authenticate();
        ReservationData[] fetchedReservations = fetchReservations();
        assertEquals(0, Objects.requireNonNull(fetchedReservations).length);
    }

    @Test
    @Order(3)
    void allReservationFetchTest() {
        authenticate();
        HttpRequest<Object> reservationRequest = HttpRequest.GET("/reservation/all-reservations").accept(APPLICATION_JSON)
                .bearerAuth(token);
        HttpResponse<ReservationData[]> reservationResponse = client.toBlocking()
                .exchange(reservationRequest, ReservationData[].class);

        assertEquals(OK, reservationResponse.getStatus());
        ReservationData[] fetchedReservations = reservationResponse.body();
        assertNotEquals(0, Objects.requireNonNull(fetchedReservations).length);
    }


    @Test
    @Order(4)
    void addReservationTest() throws InterruptedException {
        authenticate();
        addReservation(token);
        sleep(1000);
        ReservationData[] fetchedReservations = fetchReservations();
        assertEquals(1, Objects.requireNonNull(fetchedReservations).length);
        checkReservationData(fetchedReservations);
    }

    @Test
    @Order(5)
    void addDuplicatedReservationTest() {
        authenticate();

        ReservationData[] fetchedReservations = fetchReservations();
        assertEquals(1, Objects.requireNonNull(fetchedReservations).length);

        HttpRequest<ReservationData> addReservationRequest = HttpRequest.POST(
                        "/reservation/add-reservation",
                        reservationData
                ).accept(APPLICATION_JSON)
                .bearerAuth(token);
        assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(addReservationRequest),
                "Reservation already exists"
        );

    }

    @Test
    @Order(6)
    void editReservationTest() throws InterruptedException {
        authenticate();

        ReservationData[] fetchedReservations = fetchReservations();
        assertEquals(1, Objects.requireNonNull(fetchedReservations).length);

        EditReservationRequest editReservationData = new EditReservationRequest(
                fetchedReservations[0].getId(),
                15,
                "02.02.1970",
                USERNAME
        );

        HttpRequest<EditReservationRequest> editReservationRequest = HttpRequest.POST(
                        "/reservation/edit-reservation",
                        editReservationData
                ).accept(APPLICATION_JSON)
                .bearerAuth(token);
        HttpResponse<String> editReservationResponse = client.toBlocking().exchange(editReservationRequest);

        assertEquals(OK, editReservationResponse.getStatus());

        sleep(1000);

        fetchedReservations = fetchReservations();

        assertEquals(1, Objects.requireNonNull(fetchedReservations).length);
        assertNotEquals(reservationData.getTime(), fetchedReservations[0].getTime());
        assertNotEquals(reservationData.getDate(), fetchedReservations[0].getDate());
        assertEquals(editReservationData.getEditTime(), fetchedReservations[0].getTime());
        assertEquals(editReservationData.getEditDate(), fetchedReservations[0].getDate());
    }

    @Test
    @Order(7)
    void deleteReservationTest() throws InterruptedException {
        authenticate();

        ReservationData[] fetchedReservations = fetchReservations();
        assertEquals(1, Objects.requireNonNull(fetchedReservations).length);

        HttpRequest<String> deleteReservationRequest = HttpRequest.POST(
                        "/reservation/delete-reservation",
                        fetchedReservations[0].getId()
                )
                .accept(APPLICATION_JSON)
                .bearerAuth(token);

        HttpResponse<String> deleteReservationResponse = client.toBlocking().exchange(deleteReservationRequest);
        assertEquals(OK, deleteReservationResponse.getStatus());

        sleep(1000);

        ReservationData[] refetchedReservations = fetchReservations();
        assertEquals(0, Objects.requireNonNull(refetchedReservations).length);
    }

    @Test
    @Order(8)
    void deleteNonExistingReservationTest() {
        authenticate();

        ReservationData[] fetchedReservations = fetchReservations();
        assertEquals(0, Objects.requireNonNull(fetchedReservations).length);

        HttpRequest<Object> deleteReservationRequest = HttpRequest.POST(
                        "/reservation/delete-reservation",
                        null
                )
                .accept(APPLICATION_JSON)
                .bearerAuth(token);

        assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(deleteReservationRequest),
                "Reservation does not exist"
        );
    }

    @Test
    @Order(9)
    void editNonExistingReservationTest() {
        authenticate();

        ReservationData[] fetchedReservations = fetchReservations();
        assertEquals(0, Objects.requireNonNull(fetchedReservations).length);

        EditReservationRequest editReservationData = new EditReservationRequest(
                "0",
                15,
                "02.02.1970",
                USERNAME
        );

        HttpRequest<EditReservationRequest> editReservationRequest = HttpRequest.POST(
                        "/reservation/edit-reservation",
                        editReservationData
                ).accept(APPLICATION_JSON)
                .bearerAuth(token);
        assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(editReservationRequest),
                "Reservation does not exist"
        );
    }

    @Test
    @Order(10)
    void addReservationWithInvalidDataTest() {
        authenticate();

        ReservationData invalidReservationData = new ReservationData(
                USERNAME,
                "02.02.1970",
                100,
                "room 1"
        );

        addWrongReservation(invalidReservationData, "Invalid data");
        invalidReservationData.setDate(null);
        addWrongReservation(invalidReservationData, "Invalid data");
        invalidReservationData.setDate("02.02.1970");
        invalidReservationData.setRoom(null);
        addWrongReservation(invalidReservationData, "Invalid data");
    }*/
}

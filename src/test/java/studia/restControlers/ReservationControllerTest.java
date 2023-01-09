package studia.restControlers;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuthException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;
import studia.datatypes.EditReservationRequest;
import studia.datatypes.ReservationData;
import studia.datatypes.UserDetails;
import studia.service.Firebase;
import studia.utils.ReservationUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static io.micronaut.http.HttpStatus.OK;
import static io.micronaut.http.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static io.micronaut.http.HttpStatus.UNAUTHORIZED;
import static io.micronaut.http.MediaType.TEXT_PLAIN;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationControllerTest {
    @Inject
    JwtTokenGenerator jwtTokenGenerator;

    @Inject
    Firebase firebase;

    @MockBean(Firebase.class)
    Firebase firebase() {
        return mock(Firebase.class);
    }

    private static final String USER_ID = "test_user_id";
    private static final String USER_EMAIL = "test@email.com";
    private static final UserDetails USER = new UserDetails(USER_ID, Collections.emptyList(), Collections.emptyMap());

    private final ReservationData reservationData = new ReservationData(
            "test_4", USER_ID, "04.01.1970", 9, "room 1"
    );

    private String token;
    private List<ReservationData> reservationsList = new ArrayList<ReservationData>(
            List.of(
                    new ReservationData("test_1", USER_ID, "01.01.1970", 12, "room 1"),
                    new ReservationData("test_2", USER_ID, "02.01.1970", 11, "room 1"),
                    new ReservationData("test_3", USER_ID, "03.01.1970", 10, "room 1"))
    );

    private void setToken(String token) {
        this.token = token;
    }

    private void authenticate() {
        jwtTokenGenerator.generateToken(USER, 120).ifPresent(this::setToken);
    }

    private List<ReservationData> list(QuerySnapshot q) {
        return ReservationUtils.mapReservations(q);
    }

    private ReservationData[] fetchReservations(boolean fetchAll) throws ExecutionException, InterruptedException, FirebaseAuthException {


        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final Query userQuery = mock(Query.class);
        final Query dateQuery = mock(Query.class);
        final Query timeQuery = mock(Query.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);
        final QuerySnapshot querySnapshot = mock(QuerySnapshot.class);


        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("reservations")).thenReturn(collectionReference);
        if (!fetchAll) {
            when(collectionReference.whereEqualTo(eq("user"), any())).thenReturn(userQuery);
            when(userQuery.orderBy("date", Query.Direction.ASCENDING)).thenReturn(dateQuery);
        } else {
            when(collectionReference.orderBy("date", Query.Direction.ASCENDING)).thenReturn(dateQuery);
        }
        when(dateQuery.orderBy("time", Query.Direction.ASCENDING)).thenReturn(timeQuery);
        when(timeQuery.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);
        try (MockedStatic<ReservationUtils> providerMockedStatic = mockStatic(ReservationUtils.class)) {
            providerMockedStatic.when(() -> ReservationUtils.mapReservations(querySnapshot))
                    .thenReturn(reservationsList);

            HttpRequest<Object> reservationRequest = HttpRequest
                    .GET("/reservation/" + (fetchAll ? "all-reservations" : "my-reservations"))
                    .accept(APPLICATION_JSON)
                    .bearerAuth(token);
            HttpResponse<ReservationData[]> reservationResponse = client.toBlocking()
                    .exchange(reservationRequest, ReservationData[].class);
            assertEquals(OK, reservationResponse.getStatus());

            return ReservationUtils.mapReservations(querySnapshot).toArray(ReservationData[]::new);
        }

    }


    private void addReservation(String token) throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final Query roomQuery = mock(Query.class);
        final Query dateQuery = mock(Query.class);
        final Query timeQuery = mock(Query.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);
        final QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        final DocumentReference documentReference = mock(DocumentReference.class);
        final ApiFuture writeApiFuture = mock(ApiFuture.class);


        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("reservations")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("date", reservationData.getDate())).thenReturn(dateQuery);
        when(dateQuery.whereEqualTo("time", reservationData.getTime())).thenReturn(timeQuery);
        when(timeQuery.whereEqualTo("room", reservationData.getRoom())).thenReturn(roomQuery);
        when(roomQuery.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);
        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.set(any())).thenReturn(writeApiFuture);

        HttpRequest<ReservationData> addReservationRequest = HttpRequest.POST("/reservation/add-reservation",
                        reservationData)
                .accept(APPLICATION_JSON)
                .bearerAuth(token);
        HttpResponse<String> addReservationResponse = client.toBlocking().exchange(addReservationRequest);

        assertEquals(OK, addReservationResponse.getStatus());
        reservationsList.add(reservationData);

    }

    private void addWrongReservation(ReservationData data, String errorMessage) throws ExecutionException, InterruptedException {

        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final Query roomQuery = mock(Query.class);
        final Query dateQuery = mock(Query.class);
        final Query timeQuery = mock(Query.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);
        final QuerySnapshot querySnapshot = mock(QuerySnapshot.class);


        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("reservations")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("date", reservationData.getDate())).thenReturn(dateQuery);
        when(dateQuery.whereEqualTo("time", reservationData.getTime())).thenReturn(timeQuery);
        when(timeQuery.whereEqualTo("room", reservationData.getRoom())).thenReturn(roomQuery);
        when(roomQuery.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);

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

    private void editReservation(EditReservationRequest editReservationData) throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final DocumentReference documentReference = mock(DocumentReference.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);
        final DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        final ApiFuture writeApiFuture = mock(ApiFuture.class);

        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("reservations")).thenReturn(collectionReference);
        when(collectionReference.document(editReservationData.getReservationId())).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentReference.set(any())).thenReturn(writeApiFuture);

        HttpRequest<EditReservationRequest> editReservationRequest = HttpRequest.POST(
                        "/reservation/edit-reservation",
                        editReservationData
                ).accept(APPLICATION_JSON)
                .bearerAuth(token);
        HttpResponse<String> editReservationResponse = client.toBlocking()
                .exchange(editReservationRequest);

        assertEquals(OK, editReservationResponse.getStatus());

        reservationsList.stream()
                .filter(reservationData -> reservationData.getId().equals(editReservationData.getReservationId()))
                .findFirst()
                .ifPresent((reservationData) -> {
                    reservationData.setUser(editReservationData.getEditUser());
                    reservationData.setDate(editReservationData.getEditDate());
                    reservationData.setTime(editReservationData.getEditTime());
                });

    }

    private void editWrongReservation(EditReservationRequest editReservationData) throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        when(firebase.getDb()).thenReturn(firestore);

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

    private void deleteReservation(String reservationId) throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final DocumentReference documentReference = mock(DocumentReference.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);

        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("reservations")).thenReturn(collectionReference);
        when(collectionReference.document(reservationId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);

        HttpRequest<String> deleteReservationRequest = HttpRequest.POST(
                        "/reservation/delete-reservation",
                        reservationId
                )
                .accept(APPLICATION_JSON)
                .bearerAuth(token);

        HttpResponse<String> deleteReservationResponse = client.toBlocking().exchange(deleteReservationRequest);
        assertEquals(OK, deleteReservationResponse.getStatus());

        reservationsList.removeIf(reservationData -> reservationData.getId().equals(reservationId));
    }

    private void deleteWrongReservation() throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        when(firebase.getDb()).thenReturn(firestore);

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

    private void checkReservationData(ReservationData[] reservations) {
        ReservationData testReservation = Objects.requireNonNull(reservations)[reservations.length - 1];
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
    void usersReservationFetchTest() throws
            ExecutionException,
            InterruptedException,
            FirebaseAuthException {
        authenticate();
        ReservationData[] fetchedReservations = fetchReservations(false);
        assertEquals(reservationsList.size(), Objects.requireNonNull(fetchedReservations).length);
    }

    @Test
    @Order(3)
    void allReservationFetchTest() throws ExecutionException, InterruptedException, FirebaseAuthException {
        authenticate();

        ReservationData[] fetchedReservations = fetchReservations(true);
        assertEquals(reservationsList.size(), Objects.requireNonNull(fetchedReservations).length);
    }


    @Test
    @Order(4)
    void addReservationTest() throws InterruptedException, ExecutionException, FirebaseAuthException {
        authenticate();
        addReservation(token);

        ReservationData[] fetchedReservations = fetchReservations(false);
        assertEquals(reservationsList.size(), Objects.requireNonNull(fetchedReservations).length);
        checkReservationData(fetchedReservations);
    }

    //
    @Test
    @Order(5)
    void addDuplicatedReservationTest() throws ExecutionException, InterruptedException, FirebaseAuthException {
        authenticate();

        ReservationData[] fetchedReservations = fetchReservations(false);
        assertEquals(reservationsList.size(), Objects.requireNonNull(fetchedReservations).length);
        addWrongReservation(reservationData, "Reservation already exists");
    }

    @Test
    @Order(6)
    void editReservationTest() throws InterruptedException, ExecutionException, FirebaseAuthException {
        authenticate();

        ReservationData[] fetchedReservations = fetchReservations(false);
        assertEquals(reservationsList.size(), Objects.requireNonNull(fetchedReservations).length);

        EditReservationRequest editReservationData = new EditReservationRequest(
                fetchedReservations[0].getId(),
                15,
                "02.02.1970",
                USER_ID
        );

        ReservationData fetchedReservation = fetchedReservations[0];
        assertNotEquals(fetchedReservation.getTime(), editReservationData.getEditTime());
        assertNotEquals(fetchedReservation.getDate(), editReservationData.getEditDate());

        editReservation(editReservationData);

        try {
            fetchedReservations = fetchReservations(false);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(reservationsList.size(), Objects.requireNonNull(fetchedReservations).length);
        assertEquals(editReservationData.getEditTime(), fetchedReservation.getTime());
        assertEquals(editReservationData.getEditDate(), fetchedReservation.getDate());
    }

    @Test
    @Order(7)
    void deleteReservationTest() throws InterruptedException, ExecutionException, FirebaseAuthException {
        authenticate();
        final int reservationsSize = reservationsList.size();

        ReservationData[] fetchedReservations = fetchReservations(false);
        assertEquals(reservationsList.size(), Objects.requireNonNull(fetchedReservations).length);

        deleteReservation(fetchedReservations[0].getId());

        ReservationData[] refetchedReservations = fetchReservations(false);
        assertEquals(reservationsSize - 1, Objects.requireNonNull(refetchedReservations).length);
    }

    @Test
    @Order(8)
    void deleteNonExistingReservationTest() throws ExecutionException, InterruptedException, FirebaseAuthException {
        authenticate();

        ReservationData[] fetchedReservations = fetchReservations(false);
        assertEquals(reservationsList.size(), Objects.requireNonNull(fetchedReservations).length);
        deleteWrongReservation();
    }

    @Test
    @Order(9)
    void editNonExistingReservationTest() throws ExecutionException, InterruptedException, FirebaseAuthException {
        authenticate();
        final int reservationsSize = reservationsList.size();
        EditReservationRequest editReservationData = new EditReservationRequest(
                "0",
                15,
                "02.02.1970",
                USER_ID
        );
        editWrongReservation(editReservationData);

        ReservationData[] fetchedReservations = fetchReservations(false);
        assertEquals(reservationsSize, Objects.requireNonNull(fetchedReservations).length);

    }

    @Test
    @Order(10)
    void addReservationWithInvalidDataTest() throws ExecutionException, InterruptedException {
        authenticate();

        ReservationData invalidReservationData = new ReservationData(
                USER_ID,
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
    }
}

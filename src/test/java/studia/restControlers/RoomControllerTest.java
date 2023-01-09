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
import studia.datatypes.EditRoomRequest;
import studia.datatypes.RoomData;
import studia.datatypes.UserDetails;
import studia.service.Firebase;
import studia.utils.RoomUtils;

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
class RoomControllerTest {
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

    private final RoomData roomData = new RoomData(
            "test_room", "test_room", 9
    );

    private String token;
    private List<RoomData> roomList = new ArrayList<RoomData>(
            List.of(
                    new RoomData("test_1", "test_1", 12),
                    new RoomData("test_2", "test_1", 11),
                    new RoomData("test_3", "test_1", 10))
    );

    private void setToken(String token) {
        this.token = token;
    }

    private void authenticate() {
        jwtTokenGenerator.generateToken(USER, 120).ifPresent(this::setToken);
    }

    private RoomData[] fetchRooms(final boolean fetchAll) throws ExecutionException, InterruptedException, FirebaseAuthException {
        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final Query userQuery = mock(Query.class);
        final Query dateQuery = mock(Query.class);
        final Query timeQuery = mock(Query.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);
        final QuerySnapshot querySnapshot = mock(QuerySnapshot.class);


        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        if (!fetchAll){
            when(collectionReference.whereEqualTo(eq("user"), any())).thenReturn(userQuery);
            when(userQuery.get()).thenReturn(apiFuture);
        }
        else{
            when(collectionReference.get()).thenReturn(apiFuture);
        }
        when(apiFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);
        try (MockedStatic<RoomUtils> providerMockedStatic = mockStatic(RoomUtils.class)) {
            providerMockedStatic.when(() -> RoomUtils.mapRooms(querySnapshot))
                    .thenReturn(roomList);

            HttpRequest<Object> roomRequest = HttpRequest
                    .GET("/rooms/" + (fetchAll ? "all-rooms" : "my-rooms"))
                    .accept(APPLICATION_JSON)
                    .bearerAuth(token);
            HttpResponse<RoomData[]> roomResponse = client.toBlocking()
                    .exchange(roomRequest, RoomData[].class);
            assertEquals(OK, roomResponse.getStatus());

            return RoomUtils.mapRooms(querySnapshot).toArray(RoomData[]::new);
        }
    }

    private void addRoom(String token) throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final Query roomQuery = mock(Query.class);
        final Query nameQuery = mock(Query.class);
        final Query sizeQuery = mock(Query.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);
        final QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        final DocumentReference documentReference = mock(DocumentReference.class);
        final ApiFuture writeApiFuture = mock(ApiFuture.class);


        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("name", roomData.getName())).thenReturn(nameQuery);
        when(nameQuery.whereEqualTo("size", roomData.getSize())).thenReturn(sizeQuery);
        when(sizeQuery.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);
        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.set(any())).thenReturn(writeApiFuture);

        HttpRequest<RoomData> addRoomRequest = HttpRequest.POST("/rooms/add-room",
                        roomData)
                .accept(APPLICATION_JSON)
                .bearerAuth(token);
        HttpResponse<String> addRoomResponse = client.toBlocking().exchange(addRoomRequest);

        assertEquals(OK, addRoomResponse.getStatus());
        roomList.add(roomData);
    }

    private void addWrongRoom(RoomData data, String errorMessage) throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final Query roomQuery = mock(Query.class);
        final Query nameQuery = mock(Query.class);
        final Query sizeQuery = mock(Query.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);
        final QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        final DocumentReference documentReference = mock(DocumentReference.class);
        final ApiFuture writeApiFuture = mock(ApiFuture.class);

        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("name", roomData.getName())).thenReturn(nameQuery);
        when(nameQuery.whereEqualTo("size", roomData.getSize())).thenReturn(sizeQuery);
        when(sizeQuery.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);
        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.set(any())).thenReturn(writeApiFuture);
        when(querySnapshot.isEmpty()).thenReturn(false);

        HttpRequest<RoomData> addRoomRequest = HttpRequest.POST(
                        "/rooms/add-room",
                        data
                ).accept(APPLICATION_JSON)
                .bearerAuth(token);
        assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(addRoomRequest),
                errorMessage
        );
    }

    private void editRoom(EditRoomRequest editRoomData) throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final DocumentReference documentReference = mock(DocumentReference.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);
        final DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        final ApiFuture writeApiFuture = mock(ApiFuture.class);

        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(editRoomData.getRoomId())).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentReference.set(any())).thenReturn(writeApiFuture);

        HttpRequest<EditRoomRequest> editRoomRequest = HttpRequest.POST(
                        "/rooms/edit-room",
                        editRoomData
                ).accept(APPLICATION_JSON)
                .bearerAuth(token);
        HttpResponse<String> editRoomResponse = client.toBlocking()
                .exchange(editRoomRequest);

        assertEquals(OK, editRoomResponse.getStatus());

        roomList.stream()
                .filter(roomData -> roomData.getId().equals(editRoomData.getRoomId()))
                .findFirst()
                .ifPresent((roomData) -> {
                    roomData.setName(editRoomData.getEditName());
                    roomData.setSize(editRoomData.getEditSize());
                });

    }

    private void editWrongRooms(EditRoomRequest editRoomData) throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        when(firebase.getDb()).thenReturn(firestore);

        HttpRequest<EditRoomRequest> editRoomRequest = HttpRequest.POST(
                        "/rooms/edit-room",
                        editRoomData
                ).accept(APPLICATION_JSON)
                .bearerAuth(token);
        assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(editRoomRequest),
                "Room does not exist"
        );
    }

    private void deleteRoom(String roomId) throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        final CollectionReference collectionReference = mock(CollectionReference.class);
        final DocumentReference documentReference = mock(DocumentReference.class);
        final ApiFuture apiFuture = mock(ApiFuture.class);

        when(firebase.getDb()).thenReturn(firestore);
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(roomId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);

        HttpRequest<String> deleteRoomRequest = HttpRequest.POST(
                        "/rooms/delete-room",
                        roomId
                )
                .accept(APPLICATION_JSON)
                .bearerAuth(token);

        HttpResponse<String> deleteRoomResponse = client.toBlocking().exchange(deleteRoomRequest);
        assertEquals(OK, deleteRoomResponse.getStatus());

        roomList.removeIf(roomData -> roomData.getId().equals(roomId));
    }

    private void deleteWrongRoom() throws ExecutionException, InterruptedException {
        final Firestore firestore = mock(Firestore.class);
        when(firebase.getDb()).thenReturn(firestore);

        HttpRequest<Object> deleteRoomRequest = HttpRequest.POST(
                        "/rooms/delete-room",
                        null
                )
                .accept(APPLICATION_JSON)
                .bearerAuth(token);

        assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(deleteRoomRequest),
                "Invalid data"
        );
    }

    private void checkRoomData(RoomData[] rooms) {
        RoomData testRoom = Objects.requireNonNull(rooms)[rooms.length - 1];
        assertEquals(roomData.getName(), testRoom.getName());
        assertEquals(roomData.getSize(), testRoom.getSize());
    }


    @Inject
    @Client("/")
    HttpClient client;


    @Test
    @Order(1)
    void controllerTestUnauthorized() {
        assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.GET("/rooms/my-rooms").accept(TEXT_PLAIN)));

        try {
            client.toBlocking().exchange(HttpRequest.GET("/rooms/my-rooms").accept(TEXT_PLAIN));
        } catch (HttpClientResponseException e) {
            assertEquals(e.getStatus(), UNAUTHORIZED);
        }
    }


    @Test
    @Order(2)
    void usersRoomsFetchTest() throws
            ExecutionException,
            InterruptedException,
            FirebaseAuthException {
        authenticate();
        RoomData[] fetchedRooms = fetchRooms(false);
        assertEquals(roomList.size(), Objects.requireNonNull(fetchedRooms).length);
    }

    @Test
    @Order(3)
    void allRoomsFetchTest() throws ExecutionException, InterruptedException, FirebaseAuthException {
        authenticate();
        RoomData[] fetchedRooms = fetchRooms(true);
        assertEquals(roomList.size(), Objects.requireNonNull(fetchedRooms).length);
    }


    @Test
    @Order(4)
    void addRoomTest() throws InterruptedException, ExecutionException, FirebaseAuthException {
        authenticate();
        addRoom(token);

        RoomData[] fetchedRooms = fetchRooms(false);
        assertEquals(roomList.size(), Objects.requireNonNull(fetchedRooms).length);
        checkRoomData(fetchedRooms);
    }

    //
    @Test
    @Order(5)
    void addDuplicateRoom() throws ExecutionException, InterruptedException, FirebaseAuthException {
        authenticate();

        RoomData[] fetchedRooms = fetchRooms(false);
        assertEquals(roomList.size(), Objects.requireNonNull(fetchedRooms).length);
        addWrongRoom(roomData, "Room already exists");
    }

    @Test
    @Order(6)
    void editRoomTest() throws InterruptedException, ExecutionException, FirebaseAuthException {
        authenticate();

        RoomData[] fetchedRooms = fetchRooms(false);
        assertEquals(roomList.size(), Objects.requireNonNull(fetchedRooms).length);

        EditRoomRequest editRoomData = new EditRoomRequest(
                fetchedRooms[0].getId(),
                99,
                "edit_test"
        );

        RoomData fetchedRoom = fetchedRooms[0];
        assertNotEquals(fetchedRoom.getName(), editRoomData.getEditName());
        assertNotEquals(fetchedRoom.getSize(), editRoomData.getEditSize());

        editRoom(editRoomData);

        try {
            fetchedRooms = fetchRooms(false);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(roomList.size(), Objects.requireNonNull(fetchedRooms).length);
        assertEquals(editRoomData.getEditName(), fetchedRoom.getName());
        assertEquals(editRoomData.getEditSize(), fetchedRoom.getSize());
    }

    @Test
    @Order(7)
    void deleteRoomTest() throws InterruptedException, ExecutionException, FirebaseAuthException {
        authenticate();
        final int roomSize = roomList.size();

        RoomData[] fetchedRooms = fetchRooms(false);
        assertEquals(roomList.size(), Objects.requireNonNull(fetchedRooms).length);

        deleteRoom(fetchedRooms[0].getId());

        RoomData[] refetchedRooms = fetchRooms(false);
        assertEquals(roomSize - 1, Objects.requireNonNull(refetchedRooms).length);
    }

    @Test
    @Order(8)
    void deleteNonExistingRoomTest() throws ExecutionException, InterruptedException, FirebaseAuthException {
        authenticate();

        RoomData[] fetchedRooms = fetchRooms(false);
        assertEquals(roomList.size(), Objects.requireNonNull(fetchedRooms).length);
        deleteWrongRoom();
    }

    @Test
    @Order(9)
    void editNonExistingRoomTest() throws ExecutionException, InterruptedException, FirebaseAuthException {
        authenticate();
        final int roomsSize = roomList.size();
        EditRoomRequest editRoomData = new EditRoomRequest(
                "0",
                15,
                "edit_room_non_existing"
        );
        editWrongRooms(editRoomData);

        RoomData[] fetchedRooms = fetchRooms(false);
        assertEquals(roomsSize, Objects.requireNonNull(fetchedRooms).length);
    }
}

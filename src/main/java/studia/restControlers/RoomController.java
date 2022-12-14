package studia.restControlers;

import com.google.cloud.firestore.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import studia.datatypes.*;
import studia.exceptionHandlers.ReservedRoomException;
import studia.service.Firebase;
import studia.utils.RoomUtils;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.api.core.ApiFuture;

import static io.micronaut.http.HttpHeaders.AUTHORIZATION;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/rooms")
public class RoomController {

    final String COLLECTION_NAME = "rooms";

    @Inject
    private Firebase firebase;

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/my-rooms")
    public List<RoomData> myRooms(Principal principal) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query query = db.collection(COLLECTION_NAME).whereEqualTo("user", principal.getName());

        QuerySnapshot allRooms = query.get().get();
        return RoomUtils.mapRooms(allRooms);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/all-rooms")
    public List<RoomData> index() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        CollectionReference rooms = db.collection(COLLECTION_NAME);

        QuerySnapshot allRooms = rooms.get().get();
        return allRooms.isEmpty()
                ? List.of()
                : allRooms.getDocuments().stream()
                .map((room) -> {
                    RoomData res = room.toObject(RoomData.class);
                    res.setId(room.getId());
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Post("/delete-room")
    public void deleteRoom(@Body String roomId) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (roomId == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(roomId).delete();
    }

    @Post("/edit-room")
    public void editRoom(@Body EditRoomRequest request) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (request.getRoomId() == null || request.getEditName() == null ||
                request.getEditSize() == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        DocumentReference room = db.collection(COLLECTION_NAME).document(request.getRoomId());
        DocumentSnapshot roomQuerySnapshot = room.get().get();

        if (roomQuerySnapshot.exists()) {
            room.update(
                    Map.of(
                            "name", request.getEditName(),
                            "size", request.getEditSize()
                    ));
        } else {
            throw new IllegalArgumentException("Room does not exist");
        }
    }

    @Post("/add-room")
    public void addRoom(@Body AddRoomRequest request) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (request.getName() == null || request.getSize() == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        CollectionReference rooms = db.collection(COLLECTION_NAME);
        QuerySnapshot querySnapshot = rooms
                .whereEqualTo("name", request.getName())
                .whereEqualTo("size", request.getSize())
                .get()
                .get();


        if (querySnapshot.isEmpty()) {
            DocumentReference docRef = rooms.document();
            docRef.set(
                    Map.of(
                            "name", request.getName(),
                            "size", request.getSize()
                    )
            );
        } else {
            throw new IllegalArgumentException("Room already exists");
        }
    }

    @Post("/get-available-rooms")
    public List<RoomData> getAvailableSlots(
            @Header(AUTHORIZATION) String authorization,
            @Body DatetimeData term,
            Principal principal
    ) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query teamQuery = db.collection("teams").whereEqualTo("teamLeader", principal.getName());
        System.out.println(principal.getName());
        QuerySnapshot teamDocuments = teamQuery.get().get();
        if (teamDocuments.isEmpty()) {
            throw new IllegalArgumentException("You are not a team leader");
        }
        TeamData team = teamDocuments.getDocuments().get(0).toObject(TeamData.class);

        Query reservationsQuery = db.collection("reservations")
                .whereEqualTo("date", term.getDate())
                .whereEqualTo("time", term.getTime());

        QuerySnapshot reservationsDocuments = reservationsQuery.get().get();
        List<ReservationData> reservedRooms = reservationsDocuments.getDocuments().stream()
                .map((snapshot) -> snapshot.toObject(ReservationData.class))
                .collect(Collectors.toList());
        Query roomsQuery = db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("size", team.getTeamMembers().size())
                .orderBy("size", Query.Direction.ASCENDING);

        QuerySnapshot roomsDocuments = roomsQuery.get().get();
        if (roomsDocuments.isEmpty()) {
            throw new IllegalArgumentException("Team doesn't fit in any room");
        }
        List<RoomData> rooms = roomsDocuments.getDocuments().stream()
                .map((snapshot) -> {
                    RoomData room = snapshot.toObject(RoomData.class);
                    room.setId(snapshot.getId());
                    return room;
                })
                .collect(Collectors.toList());
        for (ReservationData reservation : reservedRooms) {
            if (reservation.getUser().equals(principal.getName())) {
                throw new ReservedRoomException("You already have a reservation at this time");
            }
            rooms.removeIf((r) -> r.getName().equals(reservation.getRoom()));
        }

        return rooms;
    }

}

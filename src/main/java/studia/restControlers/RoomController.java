package studia.restControlers;

import com.google.cloud.firestore.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import studia.datatypes.EditRoomRequest;
import studia.datatypes.RoomData;
import studia.service.Firebase;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.api.core.ApiFuture;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/rooms")
public class RoomController {

    @Inject private Firebase firebase;

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/my-rooms")
    public List<RoomData> MyRooms() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query query = db.collection("rooms").whereEqualTo("user", "mboruwa");

        QuerySnapshot allRooms = query.get().get();
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
    @Produces(MediaType.APPLICATION_JSON)
    @Get("/all-rooms")
    public List<RoomData> index() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        CollectionReference rooms = db.collection("rooms");

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

        if(roomId == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        ApiFuture<WriteResult> writeResult = db.collection("rooms").document(roomId).delete();

        System.out.println("Update time : " + writeResult.get().getUpdateTime());
    }

    @Post("/edit-room")
    public void editRoom(@Body EditRoomRequest request) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if(request.getRoomId() == null || request.getEditName() == null ||
           request.getEditSize() == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        DocumentReference room = db.collection("rooms").document(request.getRoomId());
        DocumentSnapshot roomQuerySnapshot = room.get().get();

        if (roomQuerySnapshot.exists()) {
            ApiFuture<WriteResult> result = room.update(
                    Map.of(
                            "name", request.getEditName(),
                            "size", request.getEditSize()
                    ));
        } else {
            throw new IllegalArgumentException("Room doesn't exist");
        }
    }

    // @Post("/add-room")
    // public void addReservation(@Body ReservationData data) throws InterruptedException, ExecutionException {
    //     Firestore db = firebase.getDb();


    //     if(data.getUser() == null || data.getDate() == null || data.getTime() == null || data.getRoom() == null) {
    //         throw new IllegalArgumentException("Invalid data");
    //     }

    //     CollectionReference reservations = db.collection("reservations");
    //     QuerySnapshot querySnapshot = reservations
    //             .whereEqualTo("date", data.getDate())
    //             .whereEqualTo("time", data.getTime())
    //             .whereEqualTo("room", data.getRoom())
    //             .get()
    //             .get();


    //     if(querySnapshot.isEmpty()) {
    //         DocumentReference docRef = reservations.document();
    //         ApiFuture<WriteResult> result = docRef.set(
    //                 Map.of(
    //                         "user", data.getUser(),
    //                         "date", data.getDate(),
    //                         "time", data.getTime(),
    //                         "room", data.getRoom()
    //                 )
    //         );
    //     } else {
    //         throw new IllegalArgumentException("Reservation already exists");
    //     }
    // }
}

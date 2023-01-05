package studia.restControlers;

import com.google.cloud.firestore.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import studia.datatypes.ReservationData;
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
    public List<Object> MyRooms() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        // Query query = db.collection("rooms").whereEqualTo("user", principal.getName());
        Query query = db.collection("rooms")
                .whereEqualTo("user", "mboruwa");

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        return documents.isEmpty()
                ? List.of()
                : documents.getDocuments().stream()
                    .map(QueryDocumentSnapshot::getData)
                    .collect(Collectors.toList());

    }
    @Produces(MediaType.APPLICATION_JSON)
    @Get("/all-rooms")
    public List<Object> index() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        CollectionReference rooms = db.collection("rooms");
        Query roomsQuery = rooms;

        ApiFuture<QuerySnapshot> querySnapshot = roomsQuery.get();
        QuerySnapshot documents = querySnapshot.get();
        return documents.isEmpty()
                ? List.of()
                : documents.getDocuments().stream()
                    .map(QueryDocumentSnapshot::getData)
                    .collect(Collectors.toList());

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

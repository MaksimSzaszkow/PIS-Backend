package studia.restControlers;

import com.google.cloud.firestore.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import studia.datatypes.DatetimeData;
import studia.datatypes.EditReservationRequest;
import studia.datatypes.ReservationData;
import studia.datatypes.RoomData;
import studia.datatypes.TeamData;
import studia.exceptionHandlers.ReservedRoomException;
import studia.service.Firebase;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.api.core.ApiFuture;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/reservation")
public class ReservationController {

    @Inject
    private Firebase firebase;

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/my-reservations")
    public List<ReservationData> MyReservations(Principal principal) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query reservationsQuery = db.collection("reservations")
                .whereEqualTo("user", principal.getName())
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("time", Query.Direction.ASCENDING);

        ApiFuture<QuerySnapshot> reservationsQuerySnapshot = reservationsQuery.get();
        QuerySnapshot reservationsDocuments = reservationsQuerySnapshot.get();
        return reservationsDocuments.isEmpty()
                ? List.of()
                : reservationsDocuments.getDocuments().stream()
                .map((snapshot) -> {
                    ReservationData res = snapshot.toObject(ReservationData.class);
                    res.setId(snapshot.getId());
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/all-reservations")
    public List<ReservationData> index() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query reservationsQuery = db.collection("reservations")
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("time", Query.Direction.ASCENDING);

        QuerySnapshot reservationsDocuments = reservationsQuery.get().get();
        return reservationsDocuments.isEmpty()
                ? List.of()
                : reservationsDocuments.getDocuments().stream()
                .map((snapshot) -> {
                    ReservationData res = snapshot.toObject(ReservationData.class);
                    res.setId(snapshot.getId());
                    return res;
                })
                .collect(Collectors.toList());

    }

    @Post("/add-reservation")
    public void addReservation(@Body ReservationData data, Principal principal) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (
                data.getDate() == null ||
                        data.getTime() > 17 ||
                        data.getTime() < 9 ||
                        data.getRoom() == null
        ) {
            throw new IllegalArgumentException("Invalid data");
        }

        CollectionReference reservations = db.collection("reservations");
        QuerySnapshot reservationsQuerySnapshot = reservations
                .whereEqualTo("date", data.getDate())
                .whereEqualTo("time", data.getTime())
                .whereEqualTo("room", data.getRoom())
                .get()
                .get();


        if (reservationsQuerySnapshot.isEmpty()) {
            ApiFuture<WriteResult> result = reservations.document().set(
                    Map.of(
                            "user", principal.getName(),
                            "date", data.getDate(),
                            "time", data.getTime(),
                            "room", data.getRoom()
                    )
            );
        } else {
            throw new IllegalArgumentException("Reservation already exists");
        }
    }


    @Post("/delete-reservation")
    public void deleteReservation(@Body String reservationId) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (reservationId == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        ApiFuture<WriteResult> writeResult = db.collection("reservations").document(reservationId).delete();

        System.out.println("Update time : " + writeResult.get().getUpdateTime());
    }

    @Post("/edit-reservation")
    public void editReservation(@Body EditReservationRequest request) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (request.getReservationId() == null || request.getEditDate() == null ||
                request.getEditTime() > 17 || request.getEditTime() < 9 || request.getEditUser() == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        DocumentReference reservation = db.collection("reservations").document(request.getReservationId());
        DocumentSnapshot reservationQuerySnapshot = reservation.get().get();

        if (reservationQuerySnapshot.exists()) {
            ApiFuture<WriteResult> result = reservation.update(
                    Map.of(
                            "user", request.getEditUser(),
                            "date", request.getEditDate(),
                            "time", request.getEditTime()
                    ));
        } else {
            throw new IllegalArgumentException("Reservation doesn't exist");
        }
    }
}

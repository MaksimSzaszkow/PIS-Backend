package studia.restControlers;

import com.google.cloud.firestore.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import studia.datatypes.EditReservationRequest;
import studia.datatypes.ReservationData;
import studia.service.Firebase;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import com.google.api.core.ApiFuture;
import studia.utils.ReservationUtils;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/reservation")
public class ReservationController {

    @Inject
    private Firebase firebase;
    private static final String COLLECTION_NAME = "reservations";

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/my-reservations")
    public List<ReservationData> myReservations(Principal principal) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query reservationsQuery = db.collection(COLLECTION_NAME)
                .whereEqualTo("user", principal.getName())
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("time", Query.Direction.ASCENDING);


        QuerySnapshot reservationsDocuments = reservationsQuery.get().get();
        return ReservationUtils.mapReservations(reservationsDocuments);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/all-reservations")
    public List<ReservationData> index() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query reservationsQuery = db.collection(COLLECTION_NAME)
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("time", Query.Direction.ASCENDING);

        QuerySnapshot reservationsDocuments = reservationsQuery.get().get();
        return ReservationUtils.mapReservations(reservationsDocuments);
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

        CollectionReference reservations = db.collection(COLLECTION_NAME);
        QuerySnapshot reservationsQuerySnapshot = reservations
                .whereEqualTo("date", data.getDate())
                .whereEqualTo("time", data.getTime())
                .whereEqualTo("room", data.getRoom())
                .get()
                .get();


        if (reservationsQuerySnapshot.isEmpty()) {
            reservations.document().set(
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
    public void deleteReservation(@Body String reservationId) {
        Firestore db = firebase.getDb();

        if (reservationId == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        db.collection(COLLECTION_NAME).document(reservationId).delete();

    }

    @Post("/edit-reservation")
    public void editReservation(@Body EditReservationRequest request) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (request.getReservationId() == null || request.getEditDate() == null ||
                request.getEditTime() > 17 || request.getEditTime() < 9 || request.getEditUser() == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        DocumentReference reservation = db.collection(COLLECTION_NAME).document(request.getReservationId());
        DocumentSnapshot reservationQuerySnapshot = reservation.get().get();

        if (reservationQuerySnapshot.exists()) {
            reservation.update(
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

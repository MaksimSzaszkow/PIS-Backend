package studia.restControllers;

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
import javax.management.Query;

import com.google.api.core.ApiFuture;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/teams")
public class TeamController {

    @Inject
    private Firebase firebase;

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/my-teams")
    public List<Object> MyReservations() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query query = db.collection("teams")
                .whereEqualTo("user", "aborek")
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("time", Query.Direction.ASCENDING);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        return documents.isEmpty()
                ? List.of()
                : documents.getDocuments().stream()
                        .map(QueryDocumentSnapshot::getData)
                        .collect(Collectors.toList());

    }

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/all-teams")
    public List<Object> MyReservations() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query query = db.collection("teams")
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("time", Query.Direction.ASCENDING);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        return documents.isEmpty()
                ? List.of()
                : documents.getDocuments().stream()
                        .map(QueryDocumentSnapshot::getData)
                        .collect(Collectors.toList());

    }

    @Post("/add-team")
    public void addTeam(@Body TeamData data) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (data.getUser() == null || data.getDate() == null || data.getTime() == null || data.getTeam() == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        CollectionReference reservations = db.collection("teams");
        QuerySnapshot querySnapshot = teams
                .whereEqualTo("date", data.getDate())
                .whereEqualTo("time", data.getTime())
                .whereEqualTo("team", data.getTeam())
                .get()
                .get();

        if (querySnapshot.isEmpty()) {
            DocumentReference docRef = teams.document();
            ApiFuture<WriteResult> result = docRef.set(
                    Map.of(
                            "user", data.getUser(),
                            "date", data.getDate(),
                            "time", data.getTime(),
                            "team", data.getTeam()
                    )
            );
        } else {
            throw new IllegalArgumentException("Team already exists");
        }

    }
}

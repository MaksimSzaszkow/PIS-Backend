package studia.restControlers;

import com.google.cloud.firestore.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import studia.datatypes.*;
import studia.service.Firebase;
import studia.utils.TeamUtils;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.api.core.ApiFuture;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/teams")
public class TeamController {

    final String COLLECTION_NAME = "teams";

    @Inject
    private Firebase firebase;

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/my-teams")
    public List<TeamData> myTeams(Principal principal) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        Query query = db.collection(COLLECTION_NAME).whereEqualTo("user", principal.getName());

        QuerySnapshot allTeams = query.get().get();
        return TeamUtils.mapTeams(allTeams);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/all-teams")
    public List<TeamData> index() throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        CollectionReference teams = db.collection(COLLECTION_NAME);

        QuerySnapshot allTeams = teams.get().get();
        return allTeams.isEmpty()
                ? List.of()
                : allTeams.getDocuments().stream()
                        .map((team) -> {
                            TeamData res = team.toObject(TeamData.class);
                            res.setId(team.getId());
                            return res;
                        })
                        .collect(Collectors.toList());
    }

    @Post("/delete-team")
    public void deleteTeam(@Body String teamId) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (teamId == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(teamId).delete();
    }

    @Post("/edit-team")
    public void editTeam(@Body EditTeamRequest request) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (request.getTeamId() == null || request.getEditName() == null ||
                request.getEditTeamLeader() == null || request.getEditTeamMembers() == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        DocumentReference team = db.collection(COLLECTION_NAME).document(request.getTeamId());
        DocumentSnapshot teamQuerySnapshot = team.get().get();

        if (teamQuerySnapshot.exists()) {
            team.update(
                    Map.of(
                            "name", request.getEditName(),
                            "teamLeader", request.getEditTeamLeader(),
                            "teamMembers", request.getEditTeamMembers()));
        } else {
            throw new IllegalArgumentException("Team does not exist");
        }
    }

    @Post("/add-team")
    public void addTeam(@Body AddTeamRequest request) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        if (request.getName() == null || request.getTeamLeader() == null || request.getTeamMembers() == null) {
            throw new IllegalArgumentException("Invalid data");
        }

        CollectionReference teams = db.collection(COLLECTION_NAME);
        QuerySnapshot querySnapshot = teams
                .whereEqualTo("name", request.getName())
                .whereEqualTo("teamLeader", request.getTeamLeader())
                .whereEqualTo("teamMembers", request.getTeamMembers())
                .get()
                .get();

        if (querySnapshot.isEmpty()) {
            DocumentReference docRef = teams.document();
            docRef.set(
                    Map.of(
                            "name", request.getName(),
                            "teamLeader", request.getTeamLeader(),
                            "teamMembers", request.getTeamMembers()));
        } else {
            throw new IllegalArgumentException("Team already exists");
        }
    }

}

package studia.utils;

import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import studia.datatypes.TeamData;

import java.util.List;
import java.util.stream.Collectors;

public class TeamUtils {
    public static List<TeamData> mapTeams(QuerySnapshot teamDocuments) {
        return teamDocuments.isEmpty()
                ? List.of()
                : teamDocuments.getDocuments().stream()
                .map((team) -> {
                    TeamData res = team.toObject(TeamData.class);
                    try {
                        res.setTeamLeader(FirebaseAuth.getInstance().getUser(res.getTeamLeader()).getEmail());
                        List<String> teamMembers = res.getTeamMembers().stream().map((member) -> {
                            try {
                                return FirebaseAuth.getInstance().getUser(member).getEmail();
                            } catch (FirebaseAuthException e) {
                                throw new RuntimeException(e);
                            }
                        }).collect(Collectors.toList());
                        res.setTeamMembers(teamMembers);
                    } catch (FirebaseAuthException e) {
                        throw new RuntimeException(e);
                    }
                    res.setId(team.getId());
                    return res;
                })
                .collect(Collectors.toList());
    }
}

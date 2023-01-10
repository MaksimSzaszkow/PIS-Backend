package studia.utils;

import com.google.cloud.firestore.QuerySnapshot;
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
                            res.setId(team.getId());
                            return res;
                        })
                        .collect(Collectors.toList());
    }
}

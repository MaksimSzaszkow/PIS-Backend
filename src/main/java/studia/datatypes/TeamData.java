package studia.datatypes;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TeamData {

    private String id;
    private String name;

    private String teamLeader;

    private List<String> teamMembers;

    public TeamData() {
    }

    public TeamData(String name, String teamLeader, List<String> teamMembers) {
        this.name = name;
        this.teamLeader = teamLeader;
        this.teamMembers = teamMembers;
    }

    public TeamData(String id, String name, String teamLeader, List<String> teamMembers) {
        this.id = id;
        this.name = name;
        this.teamLeader = teamLeader;
        this.teamMembers = teamMembers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(String teamLeader) {
        this.teamLeader = teamLeader;
    }

    public List<String> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<String> teamMembers) {
        this.teamMembers = teamMembers;
    }

}

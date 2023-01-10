package studia.datatypes;

import java.util.List;

public class AddTeamRequest {
    private String Name;
    private String TeamLeader;
    private List<String> TeamMembers;

    public AddTeamRequest() {
    }

    public AddTeamRequest(String Name, String TeamLeader, List<String> TeamMembers) {
        this.Name = Name;
        this.TeamLeader = TeamLeader;
        this.TeamMembers = TeamMembers;
    }

    public String getTeamLeader() {
        return TeamLeader;
    }

    public void setTeamLeader(String TeamLeader) {
        this.TeamLeader = TeamLeader;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public List<String> getTeamMembers() {
        return TeamMembers;
    }

    public void setTeamMembers(List<String> TeamMembers) {
        this.TeamMembers = TeamMembers;
    }
}

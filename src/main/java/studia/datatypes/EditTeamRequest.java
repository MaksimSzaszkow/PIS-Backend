package studia.datatypes;

import java.util.List;

public class EditTeamRequest {
    private String TeamId;
    private String Name;
    private String TeamLeader;
    private List<String> TeamMembers;

    public EditTeamRequest() {
    }

    public EditTeamRequest(String TeamId, String Name, String TeamLeader, List<String> TeamMembers) {
        this.TeamId = TeamId;
        this.Name = Name;
        this.TeamLeader = TeamLeader;
        this.TeamMembers = TeamMembers;
    }

    public String getTeamId() {
        return TeamId;
    }

    public void setTeamId(String TeamId) {
        this.TeamId = TeamId;
    }

    public String getEditTeamLeader() {
        return TeamLeader;
    }

    public void setEditTeamLeader(String TeamLeader) {
        this.TeamLeader = TeamLeader;
    }

    public String getEditName() {
        return Name;
    }

    public void setEditName(String Name) {
        this.Name = Name;
    }

    public List<String> getEditTeamMembers() {
        return TeamMembers;
    }

    public void setEditTeamMembers(List<String> TeamMembers) {
        this.TeamMembers = TeamMembers;
    }
}

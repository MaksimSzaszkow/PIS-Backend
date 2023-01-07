package studia.datatypes;

public class EditRoomRequest {
    private String roomId;
    private Integer editSize;
    private String editName;

    public EditRoomRequest() {
    }

    public EditRoomRequest(String roomId, Integer editSize, String editName) {
        this.roomId = roomId;
        this.editSize = editSize;
        this.editName = editName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getEditSize() {
        return editSize;
    }

    public void setEditSize(Integer editSize) {
        this.editSize = editSize;
    }

    public String getEditName() {
        return editName;
    }

    public void setEditName(String editName) {
        this.editName = editName;
    }
}

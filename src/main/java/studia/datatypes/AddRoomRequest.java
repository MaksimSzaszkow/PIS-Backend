package studia.datatypes;

public class AddRoomRequest {
    private String Name;
    private Integer Size;

    public AddRoomRequest() {
    }

    public AddRoomRequest(String Name, Integer Size) {
        this.Name = Name;
        this.Size = Size;
    }

    public Integer getSize() {
        return Size;
    }

    public void setSize(Integer Size) {
        this.Size = Size;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
}

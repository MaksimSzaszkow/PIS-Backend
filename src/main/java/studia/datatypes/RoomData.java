package studia.datatypes;

public class RoomData {

    private String id;
    private String name;
    private int size;

    public RoomData() {
    }

    public RoomData(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public RoomData(String id, String name, int size) {
        this.id = id;
        this.name = name;
        this.size = size;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

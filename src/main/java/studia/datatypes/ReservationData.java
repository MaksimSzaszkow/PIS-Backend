package studia.datatypes;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ReservationData {

    @NotBlank
    @NotNull
    private String user;

    @NotBlank
    @NotNull
    private String date;

    @NotBlank
    @NotNull
    private int time;

    @NotBlank
    @NotNull
    private String room;

    public ReservationData() {
    }

    public ReservationData(String user, String date, int time, String room) {
        this.user = user;
        this.date = date;
        this.time = time;
        this.room = room;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        if(time < 9 || time > 17) {
            throw new IllegalArgumentException("Invalid time");
        }
        this.time = time;
    }

    public String getRoom() {
        return room;
    }


    public void setRoom(String room) {
        this.room = room;
    }


}

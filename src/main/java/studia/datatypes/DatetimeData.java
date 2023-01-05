package studia.datatypes;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DatetimeData {

    @NotBlank
    @NotNull
    private String date;
    private int time;

    public DatetimeData() {
    }

    public DatetimeData(String date, int time) {
        this.date = date;
        this.time = time;
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
        if(time > 17 || time < 9) {
            throw new IllegalArgumentException("Invalid time");
        }
        this.time = time;
    }
}

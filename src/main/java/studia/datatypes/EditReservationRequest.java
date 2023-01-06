package studia.datatypes;

public class EditReservationRequest {
    private String reservationId;
    private Integer editTime;
    private String editDate;
    private String editUser;

    public EditReservationRequest() {
    }

    public EditReservationRequest(String reservationId, Integer editTime, String editDate, String editUser) {
        this.reservationId = reservationId;
        this.editTime = editTime;
        this.editDate = editDate;
        this.editUser = editUser;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getEditTime() {
        return editTime;
    }

    public void setEditTime(Integer editTime) {
        this.editTime = editTime;
    }

    public String getEditDate() {
        return editDate;
    }

    public void setEditDate(String editDate) {
        this.editDate = editDate;
    }

    public String getEditUser() {
        return editUser;
    }

    public void setEditUser(String editUser) {
        this.editUser = editUser;
    }
}

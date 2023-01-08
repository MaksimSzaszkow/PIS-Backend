package studia.exceptionHandlers;

public class ReservedRoomException extends RuntimeException {
    public ReservedRoomException(String message) {
        super(message);
    }
}

package kz.andersen.java_intensive_13.exception;

public class AlreadyReservedException extends RuntimeException{
    public AlreadyReservedException(String message) {
        super(message);
    }
}

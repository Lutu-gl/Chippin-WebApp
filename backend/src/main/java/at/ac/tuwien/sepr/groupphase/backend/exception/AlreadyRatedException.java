package at.ac.tuwien.sepr.groupphase.backend.exception;

public class AlreadyRatedException extends Exception {
    public AlreadyRatedException() {
    }

    public AlreadyRatedException(String message) {
        super(message);
    }

    public AlreadyRatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyRatedException(Exception e) {
        super(e);
    }

}

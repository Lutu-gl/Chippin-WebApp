package at.ac.tuwien.sepr.groupphase.backend.endpoint.exceptionhandler;

/**
 * Exception used to signal unexpected and unrecoverable errors.
 */
public class FatalException extends RuntimeException {
    public FatalException(String message) {
        super(message);
    }

}

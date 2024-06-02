package at.ac.tuwien.sepr.groupphase.backend.exception;


/**
 * This exception gets thrown when a user tries to like a recipe, when it is already liked.
 * This should never happen, since the frontend does not allow a second like input
 */
public class AlreadyRatedException extends Exception {


    public AlreadyRatedException(String message) {
        super(message);
    }


}

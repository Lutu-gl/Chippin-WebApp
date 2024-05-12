package at.ac.tuwien.sepr.groupphase.backend.exception;

public class InvalidFriendRequest extends Exception {
    public InvalidFriendRequest() {
    }

    public InvalidFriendRequest(String message) {
        super(message);
    }

    public InvalidFriendRequest(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFriendRequest(Exception e) {
        super(e);
    }
}

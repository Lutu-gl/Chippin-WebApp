package at.ac.tuwien.sepr.groupphase.backend.endpoint.exceptionhandler;

import java.util.List;

public record ValidationErrorRestDto(
    String message,
    List<String> errors
) {
}

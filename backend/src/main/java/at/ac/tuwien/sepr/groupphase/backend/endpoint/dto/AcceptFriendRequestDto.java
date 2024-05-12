package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;

public class AcceptFriendRequestDto {
    @NotNull
    private String senderEmail;

    public String getSenderEmail() {
        return senderEmail;
    }
}

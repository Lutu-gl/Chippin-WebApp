package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship;

import jakarta.validation.constraints.NotNull;

public class AcceptFriendRequestDto {
    @NotNull
    private String senderEmail;

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
}

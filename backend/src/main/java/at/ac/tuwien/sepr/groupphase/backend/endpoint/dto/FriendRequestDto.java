package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;

public class FriendRequestDto {
    @NotNull
    private String receiverEmail;

    public String getReceiverEmail() {
        return receiverEmail;
    }
}

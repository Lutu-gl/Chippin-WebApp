package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;

public interface FriendshipService {

    /**
     * Sends a friend request to the user
     *
     * @param receiverEmail the email of the friend to add
     */
    void sendFriendRequest(String receiverEmail);

}

package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship.FriendInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.InvalidFriendRequest;

import java.util.Collection;

public interface FriendshipService {

    /**
     * Sends a friend request from the sender to the receiver.
     *
     * @param senderEmail the email of the sender
     * @param receiverEmail the email of the receiver
     * @throws InvalidFriendRequest is thrown if the friend request is invalid
     */
    void sendFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest;

    /**
     * Accepts an incoming friend request.
     *
     * @param senderEmail the email of the user who send the friend request
     * @param receiverEmail the email of the user who accepts the friend request
     * @throws InvalidFriendRequest is thrown if the friend request is invalid
     */
    void acceptFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest;

    /**
     * Rejects an incoming friend request.
     *
     * @param senderEmail the email of the user who send the friend request
     * @param receiverEmail the email of the user who rejects the friend request
     * @throws InvalidFriendRequest is thrown if the friend request is invalid
     */
    void rejectFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest;

    /**
     * Retracts a friend request.
     *
     * @param senderEmail the email of the user who send the friend request
     * @param receiverEmail the email of the user to whom the friend request was sent
     * @throws InvalidFriendRequest is thrown if the friend request is invalid
     */
    void retractFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest;

    /**
     * Returns a collection containing all friend of an application user.
     *
     * @param userEmail the email of the application user
     * @return the friends
     */
    Collection<String> getFriends(String userEmail);

    /**
     * Returns a collection containing all friends of an application user with additional debt information.
     *
     * @param userEmail the email of the application user
     * @return the friends with debt information
     */
    Collection<FriendInfoDto> getFriendsWithDebtInfos(String userEmail);

    /**
     * Returns a collection containing all users who have sent a friend request to an application user.
     *
     * @param userEmail the email of the application user
     * @return the users who have sent a friend request
     */
    Collection<String> getIncomingFriendRequest(String userEmail);

    /**
     * Returns a collection containing all users to whom an application user has sent a friend request.
     *
     * @param userEmail the email of the application user
     * @return the users to whom a friend request has been sent
     */
    Collection<String> getOutgoingFriendRequest(String userEmail);
}

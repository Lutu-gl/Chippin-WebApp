package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ApplicationUser sender;

    @ManyToOne
    private ApplicationUser receiver;

    @Column(nullable = false, name = "sent_at")
    private LocalDateTime sentAt;

    @Column(nullable = false, name = "friendship_status")
    private FriendshipStatus friendshipStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getSender() {
        return sender;
    }

    public void setSender(ApplicationUser sender) {
        this.sender = sender;
    }

    public ApplicationUser getReceiver() {
        return receiver;
    }

    public void setReceiver(ApplicationUser receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public FriendshipStatus getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(FriendshipStatus friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }
}

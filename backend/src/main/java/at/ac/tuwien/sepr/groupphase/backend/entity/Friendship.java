package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

}

package nl.fhict.gamemate.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "friend_requests", uniqueConstraints = @UniqueConstraint(columnNames = {"sender_id", "receiver_id"}))
public class FriendRequest {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Profile sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Profile receiver;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRequestStatus status;
    @Column(name = "sent_at", nullable = false)
    private ZonedDateTime sentAt = ZonedDateTime.now();
    @Column(name = "responded_at")
    private ZonedDateTime respondedAt;
}


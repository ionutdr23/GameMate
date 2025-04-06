package nl.fhict.gamemate.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "user_blocks")
@IdClass(UserBlockId.class)
public class UserBlock {
    @Id
    @ManyToOne
    @JoinColumn(name = "blocker_id", nullable = false)
    private Profile blocker;
    @Id
    @ManyToOne
    @JoinColumn(name = "blocked_id", nullable = false)
    private Profile blocked;
    @Column(name = "blocked_at", nullable = false)
    private ZonedDateTime blockedAt = ZonedDateTime.now();
}


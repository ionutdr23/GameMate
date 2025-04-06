package nl.fhict.gamemate.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "friendships", uniqueConstraints = @UniqueConstraint(columnNames = {"userOneId", "userTwoId"}))
public class Friendship {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_one_id", nullable = false)
    private Profile userOne;
    @ManyToOne
    @JoinColumn(name = "user_two_id", nullable = false)
    private Profile userTwo;
}


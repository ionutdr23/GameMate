package nl.fhict.gamemate.userservice.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipStatusChangedEvent {
    private UUID userId;
    private UUID friendId;
    private String status;
    private LocalDateTime timestamp;
}

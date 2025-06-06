package nl.fhict.gamemate.socialservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

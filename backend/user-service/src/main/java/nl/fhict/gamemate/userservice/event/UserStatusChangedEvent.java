package nl.fhict.gamemate.userservice.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusChangedEvent {
    private String userId;
    private UUID profileId;
    private String nickname;
    private String avatarUrl;
    private String status;
    private LocalDateTime timestamp;
}


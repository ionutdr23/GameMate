package nl.fhict.gamemate.userservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private UUID id;
    private ProfilePreviewDto sender;
    private ProfilePreviewDto receiver;
    private LocalDateTime createdAt;
}

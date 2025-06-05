package nl.fhict.gamemate.userservice.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePreviewDto {
    private UUID id;
    private String nickname;
    private String avatarUrl;
}

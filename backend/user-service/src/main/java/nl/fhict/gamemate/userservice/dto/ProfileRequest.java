package nl.fhict.gamemate.userservice.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {
    private String nickname;
    private String bio;
    private String location;
}


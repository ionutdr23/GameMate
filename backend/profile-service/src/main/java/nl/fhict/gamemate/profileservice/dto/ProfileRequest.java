package nl.fhict.gamemate.profileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fhict.gamemate.profileservice.model.ProfileVisibility;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {
    private String username;
    private String displayName;
    private String bio;
    private String country;
    private String city;
    private ProfileVisibility profileVisibility;
    private String avatarUrl;
}

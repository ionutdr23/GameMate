package nl.fhict.gamemate.socialservice.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "userProfileMappings")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileMapping {
    @Id
    private String id;
    private String userId;
    private UUID profileId;
    private String nickname;
    private String avatarUrl;
}

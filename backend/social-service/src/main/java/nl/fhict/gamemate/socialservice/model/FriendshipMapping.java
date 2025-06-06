package nl.fhict.gamemate.socialservice.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Document(collection = "friendshipMappings")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipMapping {
    @Id
    private String id;
    private UUID profileId1;
    private UUID profileId2;

    public static String buildFriendshipId(UUID id1, UUID id2) {
        return Stream.of(id1.toString(), id2.toString())
                .sorted()
                .collect(Collectors.joining("-"));
    }
}

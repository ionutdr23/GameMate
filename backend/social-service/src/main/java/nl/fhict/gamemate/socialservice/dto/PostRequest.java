package nl.fhict.gamemate.socialservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fhict.gamemate.socialservice.model.Visibility;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String content;
    private Visibility visibility;
    private String[] tags;
}

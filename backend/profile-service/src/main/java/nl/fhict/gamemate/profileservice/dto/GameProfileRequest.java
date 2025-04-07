package nl.fhict.gamemate.profileservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class GameProfileRequest {
    private UUID gameId;
    private UUID skillLevelId;
    private List<UUID> roleIds;
    private List<String> playstyleTags;
    private List<String> platformTags;
}
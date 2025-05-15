package nl.fhict.gamemate.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProfileRequest(
        @NotBlank String nickname,
        String bio,
        String location
) {}


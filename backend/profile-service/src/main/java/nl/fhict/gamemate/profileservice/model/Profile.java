package nl.fhict.gamemate.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "auth0_id", nullable = false, unique = true)
    private String auth0Id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(name = "display_name", nullable = false)
    private String displayName;
    @Column(name = "avatar_url", nullable = false)
    private String avatarUrl;
    private String bio;
    private String country;
    private String city;
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", nullable = false)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;
    private ZonedDateTime createdAt = ZonedDateTime.now();
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<GameProfile> gameProfiles = new ArrayList<>();
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<AvailabilitySlot> availabilitySlots = new ArrayList<>();
}
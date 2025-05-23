package nl.fhict.gamemate.userservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Profile {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String userId; // Auth0 sub, globally unique

    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Nickname must be alphanumeric with no spaces")
    @Column(nullable = false, unique = true, length = 20)
    @EqualsAndHashCode.Include
    private String nickname;

    @Size(max = 250)
    @EqualsAndHashCode.Include
    private String bio;

    @Size(max = 100)
    @EqualsAndHashCode.Include
    private String location;

    @Size(max = 255)
    @Pattern(regexp = "^(https?:\\/\\/)?[\\w\\-\\.]+\\.[a-z]{2,}.*$", message = "Avatar must be a valid URL")
    @EqualsAndHashCode.Include
    private String avatarUrl;

    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @Builder.Default
    private Set<Profile> friends = new HashSet<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<GameProfile> gameProfiles = new HashSet<>();
}


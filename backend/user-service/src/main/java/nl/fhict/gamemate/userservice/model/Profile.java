package nl.fhict.gamemate.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String userId; // Auth0 sub, globally unique

    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Nickname must be alphanumeric with no spaces")
    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Size(max = 250)
    private String bio;

    @Size(max = 100)
    private String location;

    @Size(max = 255)
    @Pattern(regexp = "^(https?:\\/\\/)?[\\w\\-\\.]+\\.[a-z]{2,}.*$", message = "Avatar must be a valid URL")
    private String avatarUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


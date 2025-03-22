package nl.fhict.gamemate.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String email;
    private String avatarUrl;
    private String bio;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private String country;
    private String city;
}

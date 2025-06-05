package nl.fhict.gamemate.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "profiles")
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
    private String userId;

    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Nickname must be alphanumeric with no spaces"
    )
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
    @Pattern(
            regexp = "^(https?://)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?:/[^\\s]*)?$",
            message = "Avatar must be a valid URL"
    )
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties({
            "createdAt", "updatedAt", "friends",
            "gameProfiles", "sentFriendRequests", "receivedFriendRequests"
    })
    @Builder.Default
    private Set<Profile> friends = new HashSet<>();

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<GameProfile> gameProfiles = new HashSet<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({
            "sender",
            "receiver.friends",
            "receiver.gameProfiles",
            "receiver.sentFriendRequests",
            "receiver.receivedFriendRequests"
    })
    private Set<FriendRequest> sentFriendRequests = new HashSet<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({
            "receiver",
            "sender.friends",
            "sender.gameProfiles",
            "sender.sentFriendRequests",
            "sender.receivedFriendRequests"
    })
    private Set<FriendRequest> receivedFriendRequests = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Profile> getFriends() {
        return friends;
    }

    public void setFriends(Set<Profile> friends) {
        this.friends = friends;
    }

    public Set<GameProfile> getGameProfiles() {
        return gameProfiles;
    }

    public void setGameProfiles(Set<GameProfile> gameProfiles) {
        this.gameProfiles = gameProfiles;
    }

    public Set<FriendRequest> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public void setSentFriendRequests(Set<FriendRequest> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
    }

    public Set<FriendRequest> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    public void setReceivedFriendRequests(Set<FriendRequest> receivedFriendRequests) {
        this.receivedFriendRequests = receivedFriendRequests;
    }
}


package flab.transtalk.user.domain;

import flab.transtalk.auth.domain.AuthProvider;
import flab.transtalk.translation.domain.ChatRoomUser;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_provider_pid", columnNames = {"provider", "provider_id"}),
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "user_seq_gen",
            sequenceName = "user_seq"
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "external_id", unique = true, nullable = false, length = 36)
    private String externalId;

    @Column(name = "email")
    private String email;

    @Column(name = "birth_date", columnDefinition = "DATE")
    private LocalDate birthDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    @Builder
    private User(String name, AuthProvider provider, String providerId, String email, LocalDate birthDate) {
        this.name       = name;
        this.provider   = provider;
        this.providerId = providerId;
        this.email      = email;
        this.birthDate  = birthDate;
        // externalId 는 null -> @PrePersist에서 자동 생성
        // 외부에서 주입되지 않도록 방지
    }

    @PrePersist
    private void ensureExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
    }

    public void setProfile(Profile profile) {
        if (this.profile != null && this.profile != profile) {
            this.profile.setUser(null);
        }
        this.profile = profile;
        if (profile != null && profile.getUser() != this){
            profile.setUser(this);
        }
    }
}

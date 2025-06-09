package flab.transtalk.user.domain;

import flab.transtalk.auth.domain.AuthAccount;
import flab.transtalk.translation.domain.ChatMessage;
import flab.transtalk.translation.domain.ChatRoomUser;
import jakarta.persistence.*;
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

    @Column(name = "email")
    private String email;

    @Column(name = "external_id", unique = true, nullable = false, length = 36)
    private String externalId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AuthAccount authAccount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Builder
    private User(String email) {
        this.email      = email;
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

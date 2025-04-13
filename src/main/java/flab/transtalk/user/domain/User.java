package flab.transtalk.user.domain;

import flab.transtalk.translation.domain.ChatRoomUser;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Builder
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

    @Column(name = "birth_date", columnDefinition = "DATE")
    private LocalDate birthDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

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

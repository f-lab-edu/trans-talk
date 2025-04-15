package flab.transtalk.user.domain;

import flab.transtalk.common.enums.LanguageSelection;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "profile_seq_gen",
            sequenceName = "profile_seq"
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "self_introduction", length = 300)
    private String selfIntroduction;

    @Enumerated(EnumType.STRING)
    private LanguageSelection language;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        if (this.user != null && this.user != user){
            this.user.setProfile(null);
        }
        this.user = user;
        if (user != null && user.getProfile() != this){
            user.setProfile(this);
        }
    }
}

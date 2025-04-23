package flab.transtalk.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "post_seq_gen",
            sequenceName = "post_seq"
    )
    private Long id;

    @Column
    @Builder.Default
    private String briefContext = "";

    @Column(nullable = false, length = 512)
    private String imageKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public void setProfile(Profile profile){
        if (this.profile != null && this.profile != profile){
            this.profile.removePost(this);
        }
        this.profile = profile;
        if (profile != null && !profile.getPosts().contains(this)) {
            profile.addPost(this);
        }
    }

}
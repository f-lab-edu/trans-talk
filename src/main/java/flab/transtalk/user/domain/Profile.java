package flab.transtalk.user.domain;

import flab.transtalk.common.enums.LanguageSelection;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "birth_date", columnDefinition = "DATE")
    private LocalDate birthDate;

    @Column(name = "self_introduction", length = 300)
    private String selfIntroduction;

    @Enumerated(EnumType.STRING)
    private LanguageSelection language;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    public void setName(String name){
        this.name = name;
    }
    public void setBirthDate(LocalDate birthDate){
        this.birthDate = birthDate;
    }
    public void setSelfIntroduction(String selfIntroduction){
        this.selfIntroduction = selfIntroduction;
    }
    public void setLanguage(LanguageSelection language){
        this.language = language;
    }

    public void setUser(User user) {
        if (this.user != null && this.user != user){
            this.user.setProfile(null);
        }
        this.user = user;
        if (user != null && user.getProfile() != this){
            user.setProfile(this);
        }
    }

    public void removePost(Post post) {
        if (post == null){
            return;
        }
        this.posts.remove(post);
    }

    public void addPost(Post post) {
        if (post == null || this.getPosts().contains(post)){
            return;
        }
        this.posts.add(post);
        if (post.getProfile() != this){
            post.setProfile(this);
        }
    }
}

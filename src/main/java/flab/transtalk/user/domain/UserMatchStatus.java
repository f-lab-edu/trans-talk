package flab.transtalk.user.domain;

import flab.transtalk.config.ServiceConfigConstants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMatchStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "user_state_seq_gen",
            sequenceName = "user_state_seq"
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastMatchRequestedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private int remainingMatchRequests = ServiceConfigConstants.MAX_REMAINING_MATCH_REQUESTS;

    public void setLastMatchRequestedAt(LocalDateTime lastMatchRequestedAt) {
        this.lastMatchRequestedAt = lastMatchRequestedAt;
    }

    public void setRemainingMatchRequests(int remainingMatchRequests) {
        this.remainingMatchRequests = remainingMatchRequests;
    }

    public void setUser(User user) {
        if (this.user != null && this.user != user){
            this.user.setUserMatchStatus(null);
        }
        this.user = user;
        if (user != null && user.getUserMatchStatus() != this){
            user.setUserMatchStatus(this);
        }
    }
}

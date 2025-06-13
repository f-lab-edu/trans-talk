package flab.transtalk.user.repository;

import flab.transtalk.user.domain.UserMatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMatchStatusRepository extends JpaRepository<UserMatchStatus, Long> {
    Optional<UserMatchStatus> findByUserId(Long userId);
}

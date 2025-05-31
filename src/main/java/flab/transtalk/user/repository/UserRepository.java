package flab.transtalk.user.repository;

import flab.transtalk.auth.domain.AuthProvider;
import flab.transtalk.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.id <> :userId")
    List<User> findAllExcept(@Param("userId") Long userId, Pageable pageable);
    long countByIdNot(Long userId);

    List<User> findAllByIdIn(List<Long> ids);

    Optional<User> findByExternalId(String externalId);
}

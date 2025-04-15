package flab.transtalk.user.repository;

import flab.transtalk.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.id <> :userId ORDER BY function('RAND')")
    User findRandomUserExcept(@Param("userId") Long userId);

    List<User> findAllByIdIn(List<Long> ids);
}
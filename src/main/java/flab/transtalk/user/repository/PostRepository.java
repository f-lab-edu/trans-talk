package flab.transtalk.user.repository;

import flab.transtalk.user.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndProfile_UserId(Long postId, Long userId);
}
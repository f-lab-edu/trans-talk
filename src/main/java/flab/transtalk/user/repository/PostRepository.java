package flab.transtalk.user.repository;

import flab.transtalk.user.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
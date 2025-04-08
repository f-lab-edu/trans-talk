package flab.transtalk.translation.repository;

import flab.transtalk.translation.domain.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
}
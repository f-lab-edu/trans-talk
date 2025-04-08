package flab.transtalk.translation.dto.req;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link flab.transtalk.translation.domain.ChatRoom}
 */
@Value
public class ChatRoomCreateRequestDto {
    String title;
    List<Long> participantUserIds;
}
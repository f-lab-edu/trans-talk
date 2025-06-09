package flab.transtalk.translation.dto.req;

import lombok.Value;

@Value
public class ChatMessageRequestDto {
    Long chatRoomId;
    String content;
}

package flab.transtalk.translation.dto.req;

import lombok.Getter;

@Getter
public class ChatMessageRequestDto {
    private Long chatRoomId;
    private String content;
}

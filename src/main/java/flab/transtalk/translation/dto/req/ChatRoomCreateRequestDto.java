package flab.transtalk.translation.dto.req;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomCreateRequestDto {
    private String title;
    private List<Long> participantUserIds;
}
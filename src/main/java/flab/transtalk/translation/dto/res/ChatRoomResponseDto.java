package flab.transtalk.translation.dto.res;

import flab.transtalk.translation.domain.ChatRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomResponseDto {
    private Long id;
    private String title;
    private List<UserSummaryDto> participants;

    public static ChatRoomResponseDto from(ChatRoom chatRoom){
        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .participants(
                        chatRoom.getChatRoomUsers().stream().map(
                                chatRoomUser->UserSummaryDto.from(chatRoomUser.getUser())
                        ).collect(Collectors.toList())
                )
                .build();
    }
}
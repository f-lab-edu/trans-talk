package flab.transtalk.translation.dto.res;

import flab.transtalk.translation.domain.ChatMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageResponseDto {
    Long chatRoomId;
    Long senderId;
    String content;
    String translatedText;
    LocalDateTime createdDate;

    public static ChatMessageResponseDto from(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .chatRoomId(chatMessage.getChatRoom()!=null?
                        chatMessage.getChatRoom().getId() :
                        null
                ).senderId(chatMessage.getUser()!=null?
                        chatMessage.getUser().getId() :
                        null
                ).content(chatMessage.getContent())
                .translatedText(chatMessage.getTranslatedText())
                .createdDate(chatMessage.getCreatedDate())
                .build();
    }
}

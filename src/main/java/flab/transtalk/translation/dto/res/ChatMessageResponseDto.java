package flab.transtalk.translation.dto.res;

import flab.transtalk.translation.domain.ChatMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageResponseDto {
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private String translatedText;
    private LocalDateTime createdDate;

    public static ChatMessageResponseDto from(ChatMessage chatMessage) {
        Long chatRoomId = chatMessage.getChatRoom() != null ? chatMessage.getChatRoom().getId() : null;
        Long senderId = chatMessage.getUser() != null ? chatMessage.getUser().getId() : null;

        return ChatMessageResponseDto.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(chatMessage.getContent())
                .translatedText(chatMessage.getTranslatedText())
                .createdDate(chatMessage.getCreatedDate())
                .build();
    }
}

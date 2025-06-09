package flab.transtalk.translation.service.chatmessage;

import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.translation.domain.ChatMessage;
import flab.transtalk.translation.domain.ChatRoom;
import flab.transtalk.translation.dto.req.ChatMessageRequestDto;
import flab.transtalk.translation.dto.res.ChatMessageResponseDto;
import flab.transtalk.translation.repository.ChatMessageRepository;
import flab.transtalk.translation.repository.ChatRoomRepository;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessageResponseDto saveMessage(ChatMessageRequestDto request, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.CHATROOM_NOT_FOUND,
                        request.getChatRoomId().toString()
                ));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.USER_NOT_FOUND,
                        senderId.toString()
                ));

        boolean isParticipant = chatRoom.getChatRoomUsers().stream()
                .anyMatch(chatRoomUser -> chatRoomUser.getUser().getId().equals(senderId));
        if (!isParticipant) {
            throw new NotFoundException(
                    ExceptionMessages.USER_NOT_FOUND_IN_CHATROOM,
                    senderId.toString()
            );
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .content(request.getContent())
                .translatedText(null)
                .createdDate(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(chatMessage);
    }
}

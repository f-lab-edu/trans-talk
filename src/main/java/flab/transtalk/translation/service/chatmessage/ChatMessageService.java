package flab.transtalk.translation.service.chatmessage;

import flab.transtalk.common.exception.ExternalApiUnavailableException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.MissingMandatoryAssociationException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.translation.domain.ChatMessage;
import flab.transtalk.translation.domain.ChatRoom;
import flab.transtalk.translation.dto.req.ChatMessageRequestDto;
import flab.transtalk.translation.dto.res.ChatMessageResponseDto;
import flab.transtalk.translation.repository.ChatMessageRepository;
import flab.transtalk.translation.repository.ChatRoomRepository;
import flab.transtalk.translation.service.chatroom.ChatRoomService;
import flab.transtalk.translation.service.translation.TranslateService;
import flab.transtalk.user.domain.Profile;
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
    private final ChatRoomService chatRoomService;
    private final TranslateService translateService;

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

        boolean isParticipant = chatRoomService.isParticipant(senderId,request.getChatRoomId());
        if (!isParticipant) {
            throw new NotFoundException(
                    ExceptionMessages.USER_NOT_FOUND_IN_CHATROOM,
                    senderId.toString()
            );
        }

        Profile senderProfile = sender.getProfile();
        if (senderProfile == null){
            throw new MissingMandatoryAssociationException(
                    ExceptionMessages.PROFILE_NOT_LINKED_TO_USER
            );
        }

        User receiverUser = chatRoom.getChatRoomUsers().stream().filter(target->target.getId().equals(sender.getId())).findFirst()
                .orElseThrow(() -> new MissingMandatoryAssociationException(
                        ExceptionMessages.NOT_EXIST_RECEIVER_IN_CHATROOM
                )).getUser();
        Profile receiverProfile = receiverUser.getProfile();
        if (receiverProfile == null){
            throw new MissingMandatoryAssociationException(
                    ExceptionMessages.PROFILE_NOT_LINKED_TO_USER
            );
        }

        String translatedText;
        try {
            translatedText = translateService.translate(
                    request.getProviderSelection(),
                    request.getContent(),
                    senderProfile.getLanguage(),
                    receiverProfile.getLanguage()
            );
        } catch(IllegalArgumentException ex) {
            throw ex;
        } catch(RuntimeException ex) {
            throw new ExternalApiUnavailableException(
                    String.format(
                            ExceptionMessages.TRANSLATION_SERVICE_UNAVAILABLE,
                            (request.getProviderSelection().getProviderKey()!=null? request.getProviderSelection().getProviderKey(): "default provider")
                    ));
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .content(request.getContent())
                .translatedText(translatedText)
                .createdDate(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(chatMessage);
    }
}

package flab.transtalk.translation.service.chatroom;

import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.translation.domain.ChatRoom;
import flab.transtalk.translation.dto.req.ChatRoomCreateRequestDto;
import flab.transtalk.translation.dto.res.ChatRoomResponseDto;
import flab.transtalk.translation.repository.ChatRoomRepository;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoomResponseDto createChatRoom(ChatRoomCreateRequestDto reqDto){
        if (reqDto.getParticipantUserIds().size()<2){
            throw new BadRequestException(ExceptionMessages.CHAT_ROOM_REQUIRES_AT_LEAST_TWO_USERS);
        }
        List<User> users = userRepository.findAllByIdIn(reqDto.getParticipantUserIds());
        if (users.size() != reqDto.getParticipantUserIds().size()){
            throw new NotFoundException(
                    ExceptionMessages.USER_NOT_FOUND_IN_PARTICIPANTS,
                    Arrays.toString(reqDto.getParticipantUserIds().toArray(Long[]::new)),
                    Arrays.toString(users.stream().map(target->target.getId()).toArray(Long[]::new))
            );
        }
        ChatRoom chatRoom = ChatRoom.builder()
                .title(reqDto.getTitle())
                .build();
        users.forEach(chatRoom::addUser);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.from(savedChatRoom);
    }

    @Transactional(readOnly = true)
    public boolean isParticipant(Long userId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.CHATROOM_NOT_FOUND,
                        chatRoomId.toString()
                ));

        return chatRoom.getChatRoomUsers().stream()
                .anyMatch(chatRoomUser -> chatRoomUser.getUser().getId().equals(userId));
    }
}

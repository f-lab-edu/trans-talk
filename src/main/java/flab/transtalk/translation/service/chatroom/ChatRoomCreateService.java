package flab.transtalk.translation.service.chatroom;

import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.translation.domain.ChatRoom;
import flab.transtalk.translation.domain.ChatRoomUser;
import flab.transtalk.translation.dto.req.ChatRoomCreateRequestDto;
import flab.transtalk.translation.dto.res.ChatRoomResponseDto;
import flab.transtalk.translation.repository.ChatRoomRepository;
import flab.transtalk.translation.repository.ChatRoomUserRepository;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomCreateService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoomResponseDto createChatRoom(ChatRoomCreateRequestDto reqDto){
        if (reqDto.getParticipantUserIds().size()<2){
            throw new BadRequestException("채팅방에는 최소 2명이상 존재해야 합니다.");
        }
        List<User> users = userRepository.findAllByIdIn(reqDto.getParticipantUserIds());
        if (users.size() != reqDto.getParticipantUserIds().size()){
            throw new NotFoundException("존재하지 않는 사용자가 포함되어 있습니다.");
        }
        ChatRoom chatRoom = ChatRoom.builder()
                .title(reqDto.getTitle())
                .build();

        List<ChatRoomUser> links = users.stream()
                .map(user->ChatRoomUser.builder()
                        .user(user)
                        .chatRoom(chatRoom)
                        .build()
        ).collect(Collectors.toList());
        chatRoom.getChatRoomUsers().addAll(links);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.from(savedChatRoom);
    }
}

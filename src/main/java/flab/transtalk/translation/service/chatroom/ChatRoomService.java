package flab.transtalk.translation.service.chatroom;

import flab.transtalk.translation.dto.req.ChatRoomCreateRequestDto;
import flab.transtalk.translation.dto.res.ChatRoomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomCreateService chatRoomCreateService;

    public ChatRoomResponseDto createChatRoom(ChatRoomCreateRequestDto reqDto){
        return chatRoomCreateService.createChatRoom(reqDto);
    }

}

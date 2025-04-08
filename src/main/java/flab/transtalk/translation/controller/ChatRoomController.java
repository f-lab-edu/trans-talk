package flab.transtalk.translation.controller;

import flab.transtalk.translation.dto.req.ChatRoomCreateRequestDto;
import flab.transtalk.translation.dto.res.ChatRoomResponseDto;
import flab.transtalk.translation.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(@RequestBody ChatRoomCreateRequestDto req){
        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoomService.createChatRoom(req));
    }
}

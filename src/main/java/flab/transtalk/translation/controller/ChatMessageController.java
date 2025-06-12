package flab.transtalk.translation.controller;

import flab.transtalk.config.StompConstants;
import flab.transtalk.translation.dto.req.ChatMessageRequestDto;
import flab.transtalk.translation.dto.res.ChatMessageResponseDto;
import flab.transtalk.translation.service.chatmessage.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequestDto messageRequest,
                            Principal principal) {
        Long senderId = Long.parseLong(principal.getName());
        ChatMessageResponseDto response = chatMessageService.saveMessage(messageRequest, senderId);

        String destination = StompConstants.CHAT_ROOM_TOPIC_PREFIX + response.getChatRoomId();
        messagingTemplate.convertAndSend(destination, response);
    }
}

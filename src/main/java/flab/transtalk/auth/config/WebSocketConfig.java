package flab.transtalk.auth.config;

import flab.transtalk.auth.security.jwt.JwtHandshakeInterceptor;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.translation.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final ChatRoomService chatRoomService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS(); // for browser fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null){
                    if (accessor.getUser() == null) {
                        SecurityContext context = (SecurityContext) accessor.getSessionAttributes().get("SPRING_SECURITY_CONTEXT");
                        if (context != null) {
                            accessor.setUser(context.getAuthentication());
                        }
                    }
                    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                        String destination = accessor.getDestination();
                        Principal user = accessor.getUser();

                        if (destination == null || !destination.startsWith("/topic/chat-room.") || destination.length()=="/topic/chat-room.".length()) {
                            throw new MessagingException("잘못된 구독 경로입니다.");
                        }
                        Long chatRoomId = Long.valueOf(destination.substring("/topic/chat-room.".length()));
                        Long userId = Long.valueOf(user.getName());
                        try {
                            if (!chatRoomService.isParticipant(userId, chatRoomId)) {
                                throw new MessagingException("구독 권한이 없습니다.");
                            }
                        } catch (NotFoundException ex){
                            throw new MessagingException("존재하지 않는 채팅방입니다.");
                        }
                    }
                }

                return message;
            }
        });
    }
}

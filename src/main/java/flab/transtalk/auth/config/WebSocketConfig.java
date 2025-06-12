package flab.transtalk.auth.config;

import flab.transtalk.auth.security.jwt.JwtHandshakeInterceptor;
import flab.transtalk.auth.security.jwt.JwtTokenProvider;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.config.StompConstants;
import flab.transtalk.translation.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final ChatRoomService chatRoomService;
    private final UserDetailsService userDetailsService;

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
                if (accessor == null) {
                    throw new MessagingException("STOMP 메시지가 아닙니다.");
                }

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String raw = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
                    if (raw == null || !raw.startsWith("Bearer "))
                        throw new MessagingException("인증 정보가 없습니다.");
                    String token = raw.substring(7);
                    if (!jwtTokenProvider.validateToken(token))
                        throw new MessagingException("잘못된 인증 정보입니다.");

                    String subject = jwtTokenProvider.getSubject(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    accessor.setUser(authToken);
                    accessor.removeNativeHeader(HttpHeaders.AUTHORIZATION);
                }

                if (accessor.getUser() == null) {
                    throw new MessagingException("요청한 사용자를 식별할 수 없습니다.");
                }

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    Principal user = accessor.getUser();

                    if (destination == null || !destination.startsWith(StompConstants.CHAT_ROOM_TOPIC_PREFIX) || destination.length()==StompConstants.CHAT_ROOM_TOPIC_PREFIX.length()) {
                        throw new MessagingException("잘못된 구독 경로입니다.");
                    }
                    Long chatRoomId = Long.valueOf(destination.substring(StompConstants.CHAT_ROOM_TOPIC_PREFIX.length()));
                    Long userId = Long.valueOf(user.getName());
                    try {
                        if (!chatRoomService.isParticipant(userId, chatRoomId)) {
                            throw new MessagingException("구독 권한이 없습니다.");
                        }
                    } catch (NotFoundException ex){
                        throw new MessagingException("존재하지 않는 채팅방입니다.");
                    }
                }

                return message;
            }
        });
    }
}

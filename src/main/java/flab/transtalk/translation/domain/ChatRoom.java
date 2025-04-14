package flab.transtalk.translation.domain;

import flab.transtalk.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "chat_room_seq_gen",
            sequenceName = "chat_room_seq"
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    public void addUser(User user) {
        ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                .chatRoom(this)
                .user(user)
                .build();
        chatRoomUsers.add(chatRoomUser);
    }
}

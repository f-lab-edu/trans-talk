package flab.transtalk.user.dto.req;

import flab.transtalk.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * DTO for {@link flab.transtalk.user.domain.User}
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCreateRequestDto {
    String email;

    public User toEntity(){
        return User.builder()
                .email(email)
                .build();
    }
}

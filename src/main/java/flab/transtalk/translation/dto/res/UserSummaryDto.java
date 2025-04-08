package flab.transtalk.translation.dto.res;

import flab.transtalk.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link flab.transtalk.user.domain.User}
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSummaryDto implements Serializable {
    Long id;
    String name;

    public static UserSummaryDto from(User user){
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
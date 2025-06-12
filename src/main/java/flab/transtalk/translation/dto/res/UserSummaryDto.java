package flab.transtalk.translation.dto.res;

import flab.transtalk.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSummaryDto {
    private Long id;

    public static UserSummaryDto from(User user){
        return UserSummaryDto.builder()
                .id(user.getId())
                .build();
    }
}
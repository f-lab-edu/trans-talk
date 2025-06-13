package flab.transtalk.user.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMatchStatusResponseDto {
    private int remainingMatchRequests;
}

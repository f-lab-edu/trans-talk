package flab.transtalk.user.dto.res;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserMatchStatusResponseDto {
    int remainingMatchRequests;
}
